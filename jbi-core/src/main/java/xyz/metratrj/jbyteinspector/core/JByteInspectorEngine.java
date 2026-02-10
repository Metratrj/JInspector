package xyz.metratrj.jbyteinspector.core;

import xyz.metratrj.jbyteinspector.io.FileUtils;
import xyz.metratrj.jbyteinspector.parser.classfile.*;
import xyz.metratrj.jbyteinspector.parser.utils.AccessFlagUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JByteInspectorEngine implements AnalysisService {
    private static final Logger logger = Logger.getLogger(JByteInspectorEngine.class.getName());

    @Override
    public List<ClassReport> analyze(Path inputPath) {
        if (inputPath.toString().endsWith(".jar")) {
            return analyzeJar(inputPath);
        }
        return analyzePath(inputPath);
    }

    record ClassData(String name, byte[] content) { }

    private List<ClassData> scan(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path, Integer.MAX_VALUE)) {
            return walk
                    .parallel()
                    .filter(p -> p.toString().endsWith(".class"))
                    .map(p -> {
                        try {
                            return new ClassData(p.getFileName().toString(), Files.readAllBytes(p));
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    private List<ClassReport> analyzePath(Path path) {
        List<ClassReport> reports = new ArrayList<>();
        try {
            List<Path> classFiles = FileUtils.findFiles(path, p -> p.toString().endsWith(".class") && !p.endsWith("module-info.class") && !p.endsWith("package-info.class"));
            for (Path file : classFiles) {
                // TODO: Look at the module-info structure. Something is off with the parser when we try to parse it. So for now we need to skip util I come up with a better solution.
                if (file.endsWith("module-info.class")) {
                    logger.log(Level.INFO, "Skipping module-info.class: " + file);
                    continue;
                }
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
                CodeReport codeReport = null;

                Set<AttributeReport> attributes = new HashSet<>();
                attributes = Arrays.stream(m.getAttributes()).map(attribute_info -> {
                    String attribute_name = resolveUtf8(cf, attribute_info.getAttribute_name_index());
                    return new AttributeReport(attribute_name, attribute_info.getAttribute_length(), attribute_info.getInfo());
                }).collect(Collectors.toSet());

                for (attribute_info a : m.getAttributes()) {
                    String attribute_name = resolveUtf8(cf, a.getAttribute_name_index());
                    System.out.println("Attribute Name" + attribute_name);
                    if (attribute_name.equals("Code")) {
                        try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(a.getInfo()))) {
                            // 1. Header lesen
                            int maxStack   = inputStream.readUnsignedShort();
                            int maxLocals  = inputStream.readUnsignedShort();
                            int codeLength = inputStream.readInt();
                            System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n", maxStack, maxLocals, codeLength);

                            // 1,5. Code lesen
                            byte[] code = new byte[codeLength];
                            inputStream.readFully(code);

                            // 2. Bytecode Parsen
                            System.out.println("      bytecode disassembly:");
                            List<BytecodeParser.Instruction> instructions = BytecodeParser.parse(code);
                            for (BytecodeParser.Instruction instr : instructions) {
                                System.out.printf("        %04d: %02X -> %s %s\n", instr.pc(), instr.opcode(), instr.mnemonic(), instr.operands());
                            }

                            List<Opcodes> opcodesList = instructions.stream()
                                    .map(instr -> {
                                        try {
                                            return Opcodes.valueOf(instr.mnemonic());
                                        } catch (IllegalArgumentException e) {
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .toList();
                            System.out.println(opcodesList);

                            System.out.println();
                            int exception_table_length = inputStream.readUnsignedShort();

                            System.out.printf("      exception_table_length: %d\n", exception_table_length);

                            ExceptionTableEntry[] tableEntries = new ExceptionTableEntry[exception_table_length];
                            for (int i = 0; i < exception_table_length; i++) {
                                int start_pc   = inputStream.readUnsignedShort();
                                int end_pc     = inputStream.readUnsignedShort();
                                int handler_pc = inputStream.readUnsignedShort();
                                int catch_type = inputStream.readUnsignedShort();
                                System.out.printf("        try: %d to %d, handler: %d, type: %d\n", start_pc, end_pc, handler_pc, catch_type);
                                tableEntries[i] = new ExceptionTableEntry(start_pc, end_pc, handler_pc, catch_type);
                            }


                            int attributes_count = inputStream.readUnsignedShort();
                            System.out.printf("      attributes_count: %d\n", attributes_count);
                            AttributeReport[] attributeReports = new AttributeReport[attributes_count];
                            for (int i = 0; i < attributes_count; i++) {
                                int    attribute_name_index = inputStream.readUnsignedShort();
                                int    attribute_length     = inputStream.readInt();
                                byte[] info                 = new byte[attribute_length];
                                inputStream.readFully(info);
                                System.out.printf("        attr_index: %d, length: %d, data: %s\n", attribute_name_index, attribute_length, HexFormat.of().formatHex(info));
                            }


                            codeReport = new CodeReport(maxStack, maxLocals, codeLength, code, exception_table_length, tableEntries, attributes_count, attributeReports);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                methods.add(new MethodReport(name, desc, mFlags, attributes, codeReport));
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

        return new ClassReport(className, superName, classFlags,cf.getConstantPool(),  methods, fields);
    }

    private String resolveClassName(ClassFile cf, int index) {
        if (index == 0) {
            return "java/lang/Object"; // or null
        }
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
