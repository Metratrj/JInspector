package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.AnalysisService;
import xyz.metratrj.jbyteinspector.model.ClassReport;
import xyz.metratrj.jbyteinspector.model.FieldReport;
import xyz.metratrj.jbyteinspector.model.MethodReport;
import xyz.metratrj.jbyteinspector.parser.classfile.ClassFile;
import xyz.metratrj.jbyteinspector.parser.classfile.Opcodes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
                            try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(a.data()))) {
                                // 1. Header lesen
                                int maxStack   = inputStream.readUnsignedShort();
                                int maxLocals  = inputStream.readUnsignedShort();
                                int codeLength = inputStream.readInt();
                                System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n", maxStack, maxLocals, codeLength);

                                // 1,5. Code lesen
                                byte[] code = new byte[codeLength];
                                inputStream.readFully(code);

                                // 2. Bytecode Parsen mit ByteBuffer (WICHTIG für Alignments)
                                System.out.println("      bytecode disassembly:");
                                ByteBuffer bb = ByteBuffer.wrap(code);
                                bb.order(ByteOrder.BIG_ENDIAN); // Java Classfiles are always big endian


                                ArrayList<Opcodes> opcodes = new ArrayList<>();
                                int                pc      = 0; // Program Counter (position in Array)
                                while (bb.hasRemaining()) {
                                    pc = bb.position(); // aktuelle Position merken für Ausgabe
                                    int opcode = bb.get() & 0xFF; // Receive unsinged opcode
                                    System.out.printf("        %04d: %02X -> ", pc, opcode);

                                    switch (opcode) {
                                        // --- Einfache Instructionen (1 Byte) ---
                                        case ClassFile.NOP -> {
                                            opcodes.add(Opcodes.NOP);
                                            System.out.println("NOP");
                                        }
                                        case ClassFile.ACONST_NULL -> {
                                            opcodes.add(Opcodes.ACONST_NULL);
                                            System.out.println("ACONST_NULL");
                                        }
                                        case ClassFile.ICONST_M1 -> {
                                            opcodes.add(Opcodes.ICONST_M1);
                                            System.out.println("ICONST_M1");
                                        }
                                        case ClassFile.ICONST_0 -> {
                                            opcodes.add(Opcodes.ICONST_0);
                                            System.out.println("ICONST_0");
                                        }
                                        case ClassFile.ICONST_1 -> {
                                            opcodes.add(Opcodes.ICONST_1);
                                            System.out.println("ICONST_1");
                                        }
                                        case ClassFile.ICONST_2 -> {
                                            opcodes.add(Opcodes.ICONST_2);
                                            System.out.println("ICONST_2");
                                        }
                                        case ClassFile.ICONST_3 -> {
                                            opcodes.add(Opcodes.ICONST_3);
                                            System.out.println("ICONST_3");
                                        }
                                        case ClassFile.ICONST_4 -> {
                                            opcodes.add(Opcodes.ICONST_4);
                                            System.out.println("ICONST_4");
                                        }
                                        case ClassFile.ICONST_5 -> {
                                            opcodes.add(Opcodes.ICONST_5);
                                            System.out.println("ICONST_5");
                                        }
                                        case ClassFile.FCONST_0 -> {
                                            opcodes.add(Opcodes.FCONST_0);
                                            System.out.println("FCONST_0");
                                        }
                                        case ClassFile.FCONST_1 -> {
                                            opcodes.add(Opcodes.FCONST_1);
                                            System.out.println("FCONST_1");
                                        }
                                        case ClassFile.FCONST_2 -> {
                                            opcodes.add(Opcodes.FCONST_2);
                                            System.out.println("FCONST_2");
                                        }

                                        // --- Instruktionen mit Argumenten ---
                                        case ClassFile.LDC -> {
                                            // Nimmt 1 Byte Index
                                            int index = bb.get() & 0xFF;
                                            System.out.printf("LDC #%d\n", index);
                                            opcodes.add(Opcodes.LDC);


                                            break;
                                        }
                                        case ClassFile.GETSTATIC -> {
                                            // Nimmt 2 Bytes Index
                                            int index = bb.getShort() & 0xFFFF;
                                            System.out.printf("GETSTATIC #%d\n", index);
                                            opcodes.add(Opcodes.GETSTATIC);
                                            break;
                                        }

                                        // --- TABLE SWITCH Handling ---
                                        case ClassFile.TABLESWITCH -> {
                                            System.out.println("TABLESWITCH");
                                            // 1. Padding berechnen
                                            // Wir sind jetzt bei (pc +1). Das Padding füllt bis zur nächsten 4-Byte Grenze auf
                                            int currentPos = bb.position();
                                            int padding    = (4 - (currentPos % 4)) % 4;

                                            // Padding im Buffer überspringen
                                            for (int k = 0; k < padding; k++)
                                                 bb.get();

                                            // 2. Die 32-Bit Werte lesen
                                            int defaultOffset = bb.getInt();
                                            int low           = bb.getInt();
                                            int high          = bb.getInt();
                                            int numPairs      = high - low + 1;
                                            System.out.printf("low=%d, high=%d, default=%d\n", low, high, defaultOffset);

                                            // 3. Sprungziel lesen
                                            for (int k = 0; k < numPairs; k++) {
                                                int offset = bb.getInt();
                                                System.out.printf("              %d: %d\n", (low + k), (pc + offset));
                                            }
                                            break;
                                        }

                                        default -> {
                                            System.out.printf("UNKNOWN (Opcode %d)\n", opcode);
                                            // ACHTUNG: Wenn wir den Opcode nicht kennen, wissen wir nicht,
                                            // wie viele Bytes Argumente er hat. Der Parser wird hier wahrscheinlich crashen/falsch laufen.
                                            break;
                                        }
                                    }
                                }


//                                System.out.print("      bytecode: ");
//                                for (byte b : code) {
//                                    final int opcode = b & 0xFF;
//                                    System.out.printf("%02X(%d) ", b, opcode);
//                                    switch (opcode) {
//                                        case ClassFile.NOP:
//                                            System.out.print("NOP");
//                                            opcodes.add(Opcodes.NOP);
//                                            break;
//                                        case ClassFile.ACONST_NULL:
//                                            System.out.print("ACONST_NULL");
//                                            opcodes.add(Opcodes.ACONST_NULL);
//                                            break;
//                                        case ClassFile.ICONST_M1:
//                                            System.out.print("ICONST_M1");
//                                            opcodes.add(Opcodes.ICONST_M1);
//                                            break;
//                                        case ClassFile.ICONST_0:
//                                            System.out.print("ICONST_0");
//                                            opcodes.add(Opcodes.ICONST_0);
//                                            break;
//                                        case ClassFile.ICONST_1:
//                                            System.out.print("ICONST_1");
//                                            opcodes.add(Opcodes.ICONST_1);
//                                            break;
//                                        case ClassFile.ICONST_2:
//                                            System.out.print("ICONST_2");
//                                            opcodes.add(Opcodes.ICONST_2);
//                                            break;
//                                        case ClassFile.ICONST_3:
//                                            System.out.print("ICONST_3");
//                                            opcodes.add(Opcodes.ICONST_3);
//                                            break;
//                                        case ClassFile.ICONST_4:
//                                            System.out.print("ICONST_4");
//                                            opcodes.add(Opcodes.ICONST_4);
//                                            break;
//                                        case ClassFile.ICONST_5:
//                                            System.out.print("ICONST_5");
//                                            opcodes.add(Opcodes.ICONST_5);
//                                            break;
//                                        case ClassFile.FCONST_2:
//                                            System.out.print("FCONST_2");
//                                            opcodes.add(Opcodes.FCONST_2);
//                                            break;
//                                        case ClassFile.LDC:
//                                            System.out.print("LDC");
//                                            opcodes.add(Opcodes.LDC);
//                                            break;
//                                        case ClassFile.GETSTATIC:
//                                            System.out.print("GETSTATIC");
//                                            opcodes.add(Opcodes.GETSTATIC);
//                                            break;
//                                        default:
//                                            System.out.print("?");
//                                            break;
//                                    }
//
//                                    // System.out.printf("%s ",opcode);
//                                }

                                System.out.println(opcodes);

                                System.out.println();
                                int exception_table_length = inputStream.readUnsignedShort();
                                if (exception_table_length > 0) {
                                    System.out.printf("      exception_table_length: %d\n", exception_table_length);
                                    for (int i = 0; i < exception_table_length; i++) {
                                        int start_pc   = inputStream.readUnsignedShort();
                                        int end_pc     = inputStream.readUnsignedShort();
                                        int handler_pc = inputStream.readUnsignedShort();
                                        int catch_type = inputStream.readUnsignedShort();
                                        System.out.printf("        try: %d to %d, handler: %d, type: %d\n", start_pc, end_pc, handler_pc, catch_type);
                                    }
                                }
                                int attributes_count = inputStream.readUnsignedShort();
                                if (attributes_count > 0) {
                                    System.out.printf("      attributes_count: %d\n", attributes_count);
                                    for (int i = 0; i < attributes_count; i++) {
                                        int    attribute_name_index = inputStream.readUnsignedShort();
                                        int    attribute_length     = inputStream.readInt();
                                        byte[] info                 = new byte[attribute_length];
                                        inputStream.readFully(info);
                                        System.out.printf("        attr_index: %d, length: %d, data: %s\n", attribute_name_index, attribute_length, HexFormat.of().formatHex(info));
                                    }
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                }
            }
            System.out.println();
        }
    }
}
