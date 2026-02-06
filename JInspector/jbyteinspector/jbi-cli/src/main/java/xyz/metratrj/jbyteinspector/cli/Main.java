package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.parser.classfile.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.List;

public class Main {
    static void main(String[] args) {
        /*if (args.length == 0) {
            System.out.println("Usage: JByteInspector <path-to-classes>");
            return;
        }*/

        //Path oPath  = Paths.get(args[0]);
        Path oPath = Paths.get("jbi-cli.jar");
        //Path oPath = Paths.get("/home/metratrj/sources/JInspector/JInspector/out/production/TestModule/xyz/metratrj");

        System.out.println("Inspecting: " + oPath.toAbsolutePath());

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(oPath);

        for (ClassReport report : reports) {
            System.out.println("--------------------------------------------------");
            System.out.println("Class: " + report.className());
            System.out.println("Super: " + report.superClassName());
            System.out.println("Flags: " + report.flags());

            if (!report.fields().isEmpty()) {
                System.out.println("\nFields:");
                for (FieldReport f : report.fields()) {
                    System.out.printf("  %s %s\n", f.flags(), f.name());
                }
            }

            if (!report.methods().isEmpty()) {
                System.out.println("\nMethods:");
                for (MethodReport m : report.methods()) {
                    System.out.printf("  %s %s %s\n", m.flags(), m.name(), m.descriptor());
                    m.attributes().forEach(a -> {
                        System.out.printf("    %s\n", a.name());
                        if (a.name().equals("Code")) {
                            disassembleCode(a.data(), report);
                        }
                    });

                }
            }
            System.out.println();
        }
    }

    private static void disassembleCode(byte[] data, ClassReport report) {
        try {
            Code_attribute codeAttr = new Code_attribute(0, data.length, data);

            System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n",
                              codeAttr.getMaxStack(), codeAttr.getMaxLocals(), codeAttr.getCode().length);

            System.out.println("      bytecode disassembly:");
            List<BytecodeParser.Instruction> instructions = BytecodeParser.parse(codeAttr.getCode());

            for (BytecodeParser.Instruction insn : instructions) {

                System.out.printf("        %04d: %s %s\n", insn.pc(), insn.mnemonic(), insn.operands());

                if (usesConstantPool(insn.opcode()) && !insn.operands().isEmpty()) {
                    Object operand = insn.operands().getFirst();
                    if (operand instanceof Integer index) {
                        var cpItem = report.getConstantPoolItem(index);
                        if (cpItem != null) {
                            System.out.printf("          -> CP[%d]: %s\n", index, cpItem);
                            switch (cpItem.tag) {
                                case ICodes.CONSTANT_Methodref: {
                                    var                       methodRef   = (CONSTANT_Methodref_info) cpItem;
                                    CONSTANT_Class_info       classInfo   = report.getConstantPoolItem(methodRef.getClass_index(), CONSTANT_Class_info.class);
                                    CONSTANT_NameAndType_info nameAndType = report.getConstantPoolItem(methodRef.getName_and_type_index(), CONSTANT_NameAndType_info.class);
                                    if (classInfo != null && nameAndType != null) {
                                        CONSTANT_Utf8_info name      = report.getConstantPoolItem(nameAndType.getName_index(), CONSTANT_Utf8_info.class);
                                        CONSTANT_Utf8_info desc      = report.getConstantPoolItem(nameAndType.getDescriptor_index(), CONSTANT_Utf8_info.class);
                                        CONSTANT_Utf8_info className = report.getConstantPoolItem(classInfo.name_index, CONSTANT_Utf8_info.class);

                                        System.out.printf("            Resolved: %s.%s%s\n",
                                                          className.getValue(),
                                                          name.getValue(),
                                                          desc.getValue());

                                    }

                                    break;
                                }
                                case ICodes.CONSTANT_String: {
                                    var s   = (CONSTANT_String_info) cpItem;
                                    var str = report.getConstantPoolItem(s.getString_index(), CONSTANT_Utf8_info.class);
                                    System.out.printf("            Resolved: %s\n", str.getValue());


                                    break;
                                }
                            }


                        }
                    }
                }
            }

            if (!codeAttr.getExceptionTable().isEmpty()) {
                System.out.printf("      exception_table_length: %d\n", codeAttr.getExceptionTable().size());
                for (Code_attribute.ExceptionTableEntry entry : codeAttr.getExceptionTable()) {
                    System.out.printf("        try: %d to %d, handler: %d, type: %d\n",
                                      entry.startPc(), entry.endPc(), entry.handlerPc(), entry.catchType());
                }
            }

            if (!codeAttr.getAttributes().isEmpty()) {
                System.out.printf("      attributes_count: %d\n", codeAttr.getAttributes().size());
                for (attribute_info attr : codeAttr.getAttributes()) {
                    System.out.printf("        attr_index: %d, length: %d, data: %s\n",
                                      attr.getAttribute_name_index(), attr.getAttribute_length(), HexFormat.of().formatHex(attr.getInfo()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error disassembling code: " + e.getMessage());
        }
    }

    private static boolean usesConstantPool(int opcode) {
        return switch (opcode) {
            case ICodes.LDC, ICodes.LDC_W, ICodes.LDC2_W,
                 ICodes.GETSTATIC, ICodes.PUTSTATIC, ICodes.GETFIELD, ICodes.PUTFIELD,
                 ICodes.INVOKEVIRTUAL, ICodes.INVOKESPECIAL, ICodes.INVOKESTATIC, ICodes.INVOKEINTERFACE,
                 ICodes.INVOKEDYNAMIC,
                 ICodes.NEW, ICodes.ANEWARRAY, ICodes.CHECKCAST, ICodes.INSTANCEOF, ICodes.MULTIANEWARRAY -> true;
            default -> false;
        };
    }

    @SuppressWarnings("unused")
    private static void displayMethodCode(xyz.metratrj.jbyteinspector.parser.classfile.CodeReport code) {
        try {
            System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n",
                              code.maxStack(), code.maxLocals(), code.codeLength());
            System.out.println("      bytecode disassembly:");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
