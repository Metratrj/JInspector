package xyz.metratrj.jbyteinspector.analysis;

import xyz.metratrj.jbyteinspector.api.*;
import xyz.metratrj.jbyteinspector.core.FileUtils;
import xyz.metratrj.jbyteinspector.parser.classfile.*;
import xyz.metratrj.jbyteinspector.parser.utils.AccessFlagUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JByteInspectorEngine implements AnalysisService {
    private static final Logger logger = Logger.getLogger(JByteInspectorEngine.class.getName());

    @Override
    public List<ClassReport> analyze(Path inputPath) {
        if (inputPath.toString().endsWith(".jar")) {
            return analyzeJar(inputPath);
        }
        return analyzePath(inputPath);
    }

    private List<ClassReport> analyzePath(Path path) {
        List<ClassReport> reports = new ArrayList<>();
        try {
            List<Path> classFiles = FileUtils.findFiles(path, p -> p.toString().endsWith(".class"));

            for (Path file : classFiles) {
                try {
                    ClassFile cf = ClassFile.parse(file);
                    reports.add(generateReport(cf));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to parse class file: " + file, e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to analyze path: " + path, e);
        }
        return reports;
    }

    private List<ClassReport> analyzeJar(Path jarPath) {
        logger.info("Analyzing JAR: " + jarPath);
        try (java.nio.file.FileSystem jarFs = java.nio.file.FileSystems.newFileSystem(jarPath, (ClassLoader) null)) {
            return analyzePath(jarFs.getPath("/"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open JAR file: " + jarPath, e);
            return List.of();
        }
    }

    private ClassReport generateReport(ClassFile cf) {
        // Resolve Class Name
        String className = resolveClassName(cf, cf.getThisClass());
        String superName = resolveClassName(cf, cf.getSuperClass());
        
        Set<String> classFlags = AccessFlagUtils.extract(cf.getAccessFlags()).stream()
                .map(Enum::name).collect(Collectors.toSet());

        // Methods
        List<MethodReport> methods = new ArrayList<>();
        if (cf.getMethods() != null) {
            for (method_info m : cf.getMethods()) {
                String name = resolveUtf8(cf, m.getNameIndex());
                String desc = resolveUtf8(cf, m.getDescriptionIndex());
                Set<String> mFlags = AccessFlagUtils.extractForMethod(m.getAccessFlags()).stream()
                        .map(Enum::name).collect(Collectors.toSet());
                methods.add(new MethodReport(name, desc, mFlags));
            }
        }

        // Fields
        List<FieldReport> fields = new ArrayList<>();
        if (cf.getFields() != null) {
            for (field_info f : cf.getFields()) {
                String name = resolveUtf8(cf, f.getName_index());
                Set<String> fFlags = AccessFlagUtils.extractForField(f.getAccess_flags()).stream()
                        .map(Enum::name).collect(Collectors.toSet());
                fields.add(new FieldReport(name, fFlags));
            }
        }

        return new ClassReport(className, superName, classFlags, methods, fields);
    }

    private String resolveClassName(ClassFile cf, int index) {
        if (index == 0) return "java/lang/Object"; // or null
        var classInfo = cf.getConstantPoolItem(index, CONSTANT_Class_info.class);
        if (classInfo != null) {
            return resolveUtf8(cf, classInfo.name_index);
        }
        return "UnknownClass#" + index;
    }

    private String resolveUtf8(ClassFile cf, int index) {
        var utf8Info = cf.getConstantPoolItem(index, CONSTANT_Utf8_info.class);
        return utf8Info != null ? utf8Info.getValue() : "???";
    }
}
