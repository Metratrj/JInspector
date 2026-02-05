package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.parser.classfile.AnalysisService;
import xyz.metratrj.jbyteinspector.parser.classfile.ClassReport;
import xyz.metratrj.jbyteinspector.parser.classfile.FieldReport;
import xyz.metratrj.jbyteinspector.parser.classfile.MethodReport;
import xyz.metratrj.jbyteinspector.parser.classfile.BytecodeParser;
import xyz.metratrj.jbyteinspector.parser.classfile.Code_attribute;
import xyz.metratrj.jbyteinspector.parser.classfile.attribute_info;
import xyz.metratrj.jbyteinspector.parser.classfile.ICodes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*if (args.length == 0) {
            System.out.println("Usage: JByteInspector <path-to-classes>");
            return;
        }*/

        //Path oPath  = Paths.get(args[0]);
        Path oPath = Paths.get("jbi-examples-1.0.0.jar");
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
                 ICodes.INVOKEVIRTUAL, ICodes.INVOKESPECIAL, ICodes.INVOKESTATIC, ICodes.INVOKEINTERFACE, ICodes.INVOKEDYNAMIC,
                 ICodes.NEW, ICodes.ANEWARRAY, ICodes.CHECKCAST, ICodes.INSTANCEOF, ICodes.MULTIANEWARRAY -> true;
            default -> false;
        };
    }

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
