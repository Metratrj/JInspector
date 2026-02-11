package xyz.metratrj.jbyteinspector.core;

import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.io.FileUtils;
import xyz.metratrj.jbyteinspector.model.*;
import xyz.metratrj.jbyteinspector.parser.classfile.*;
import xyz.metratrj.jbyteinspector.parser.utils.AccessFlagUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
            List<Path> classFiles = FileUtils.findFiles(path, p -> p.toString().endsWith(".class") && !p.endsWith("module-info.class") && !p.endsWith("package-info.class"));
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
        String className = resolveClassName(cf, cf.getThisClass());
        String superName = resolveClassName(cf, cf.getSuperClass());

        Set<String> classFlags = AccessFlagUtils.extract(cf.getAccessFlags()).stream()
                                                .map(Enum::name).collect(Collectors.toSet());

        List<MethodReport> methods = new ArrayList<>();
        if (cf.getMethods() != null) {
            for (method_info m : cf.getMethods()) {
                String name = resolveUtf8(cf, m.getNameIndex());
                String desc = resolveUtf8(cf, m.getDescriptionIndex());
                Set<String> mFlags = AccessFlagUtils.extractForMethod(m.getAccessFlags()).stream()
                                                    .map(Enum::name).collect(Collectors.toSet());
                
                Set<AttributeReport> attributes = Arrays.stream(m.getAttributes())
                        .map(a -> new AttributeReport(resolveUtf8(cf, a.getAttribute_name_index()), a.getAttribute_length(), a.getInfo()))
                        .collect(Collectors.toSet());

                CodeReport codeReport = null;
                for (attribute_info a : m.getAttributes()) {
                    if (resolveUtf8(cf, a.getAttribute_name_index()).equals("Code")) {
                        codeReport = parseCodeReport(cf, a);
                        break;
                    }
                }

                methods.add(new MethodReport(name, desc, mFlags, attributes, codeReport));
            }
        }

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

    private CodeReport parseCodeReport(ClassFile cf, attribute_info codeAttr) {
        try (java.io.DataInputStream in = new java.io.DataInputStream(new java.io.ByteArrayInputStream(codeAttr.getInfo()))) {
            int maxStack = in.readUnsignedShort();
            int maxLocals = in.readUnsignedShort();
            int codeLength = in.readInt();
            byte[] code = new byte[codeLength];
            in.readFully(code);

            List<BytecodeParser.Instruction> rawInstructions = BytecodeParser.parse(code);
            List<InstructionReport> instructions = rawInstructions.stream()
                    .map(insn -> createInstructionReport(cf, insn))
                    .collect(Collectors.toList());

            int exceptionTableLength = in.readUnsignedShort();
            ExceptionTableEntry[] exceptionTable = new ExceptionTableEntry[exceptionTableLength];
            for (int i = 0; i < exceptionTableLength; i++) {
                int startPc = in.readUnsignedShort();
                int endPc = in.readUnsignedShort();
                int handlerPc = in.readUnsignedShort();
                int catchTypeIndex = in.readUnsignedShort();
                String catchType = catchTypeIndex == 0 ? "any" : resolveClassName(cf, catchTypeIndex);
                exceptionTable[i] = new ExceptionTableEntry(startPc, endPc, handlerPc, catchType);
            }

            int attributesCount = in.readUnsignedShort();
            AttributeReport[] attributes = new AttributeReport[attributesCount];
            List<LocalVariableEntry> localVariableTable = new ArrayList<>();
            for (int i = 0; i < attributesCount; i++) {
                int nameIndex = in.readUnsignedShort();
                int length = in.readInt();
                byte[] info = new byte[length];
                in.readFully(info);
                String attrName = resolveUtf8(cf, nameIndex);
                attributes[i] = new AttributeReport(attrName, length, info);

                if (attrName.equals("LocalVariableTable")) {
                    try (java.io.DataInputStream lvtIn = new java.io.DataInputStream(new java.io.ByteArrayInputStream(info))) {
                        int lvtLen = lvtIn.readUnsignedShort();
                        for (int j = 0; j < lvtLen; j++) {
                            localVariableTable.add(new LocalVariableEntry(
                                    lvtIn.readUnsignedShort(),
                                    lvtIn.readUnsignedShort(),
                                    resolveUtf8(cf, lvtIn.readUnsignedShort()),
                                    resolveUtf8(cf, lvtIn.readUnsignedShort()),
                                    lvtIn.readUnsignedShort()
                            ));
                        }
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Failed to parse LocalVariableTable", e);
                    }
                }
            }

            return new CodeReport(maxStack, maxLocals, codeLength, code, instructions, exceptionTableLength, exceptionTable, localVariableTable, attributesCount, attributes);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to parse Code attribute", e);
            return null;
        }
    }

    private InstructionReport createInstructionReport(ClassFile cf, BytecodeParser.Instruction insn) {
        List<String> operands = insn.operands().stream().map(Object::toString).collect(Collectors.toList());
        String comment = "";

        if (usesConstantPool(insn.opcode()) && !insn.operands().isEmpty()) {
            Object firstOp = insn.operands().getFirst();
            if (firstOp instanceof Integer index) {
                comment = resolveConstantPoolEntry(cf, index);
            }
        }

        return new InstructionReport(insn.pc(), insn.mnemonic(), operands, comment);
    }

    private boolean usesConstantPool(int opcode) {
        return switch (opcode) {
            case ICodes.LDC, ICodes.LDC_W, ICodes.LDC2_W,
                 ICodes.GETSTATIC, ICodes.PUTSTATIC, ICodes.GETFIELD, ICodes.PUTFIELD,
                 ICodes.INVOKEVIRTUAL, ICodes.INVOKESPECIAL, ICodes.INVOKESTATIC, ICodes.INVOKEINTERFACE,
                 ICodes.INVOKEDYNAMIC,
                 ICodes.NEW, ICodes.ANEWARRAY, ICodes.CHECKCAST, ICodes.INSTANCEOF, ICodes.MULTIANEWARRAY -> true;
            default -> false;
        };
    }

    private String resolveConstantPoolEntry(ClassFile cf, int index) {
        cp_info item = cf.getConstantPoolItem(index);
        if (item == null) return "";

        return switch (item.tag) {
            case ICodes.CONSTANT_Utf8 -> ((CONSTANT_Utf8_info) item).getValue();
            case ICodes.CONSTANT_Class -> resolveUtf8(cf, ((CONSTANT_Class_info) item).name_index);
            case ICodes.CONSTANT_String -> resolveUtf8(cf, ((CONSTANT_String_info) item).getString_index());
            case ICodes.CONSTANT_Fieldref -> {
                var ref = (CONSTANT_Fieldref_info) item;
                yield resolveClassName(cf, ref.getClass_index()) + "." + resolveNameAndType(cf, ref.getName_and_type_index());
            }
            case ICodes.CONSTANT_Methodref -> {
                var ref = (CONSTANT_Methodref_info) item;
                yield resolveClassName(cf, ref.getClass_index()) + "." + resolveNameAndType(cf, ref.getName_and_type_index());
            }
            case ICodes.CONSTANT_InterfaceMethodref -> {
                var ref = (CONSTANT_InterfaceMethodref_info) item;
                yield resolveClassName(cf, ref.getClass_index()) + "." + resolveNameAndType(cf, ref.getName_and_type_index());
            }
            case ICodes.CONSTANT_NameandType -> resolveNameAndType(cf, index);
            default -> item.toString();
        };
    }

    private String resolveNameAndType(ClassFile cf, int index) {
        var nt = cf.getConstantPoolItem(index, CONSTANT_NameAndType_info.class);
        if (nt != null) {
            return resolveUtf8(cf, nt.getName_index()) + nt.getDescriptor_index(); // Simplified, should resolve descriptor too
        }
        return "???";
    }

    private String resolveClassName(ClassFile cf, int index) {
        if (index == 0) {
            return "java/lang/Object";
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
