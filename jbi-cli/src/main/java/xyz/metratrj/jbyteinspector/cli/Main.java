package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.io.ParallelLoader;
import xyz.metratrj.jbyteinspector.model.*;
import xyz.metratrj.jbyteinspector.parser.ClassReader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar jbi-cli.jar <path-to-jar-or-class-folder>");
            return;
        }

        Path oPath = Paths.get(args[0]);
        System.out.println("Inspecting: " + oPath.toAbsolutePath());

        ParallelLoader loader = new ParallelLoader();
        try {
            List<DataRecord> records = loader.load(oPath);
            for (DataRecord record : records) {
                System.out.println("--------------------------------------------------");
                System.out.println("Source: " + record.path());
                try {
                    ClassReader reader = new ClassReader(record);
                    reader.accept(new ConsoleClassVisitor());
                } catch (Exception e) {
                    System.err.println("Error parsing class " + record.path() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading files: " + e.getMessage());
        } finally {
            loader.shutdown();
        }
    }

    private static String formatFlags(int access) {
        List<String> flags = new ArrayList<>();
        if ((access & 0x0001) != 0) flags.add("PUBLIC");
        if ((access & 0x0002) != 0) flags.add("PRIVATE");
        if ((access & 0x0004) != 0) flags.add("PROTECTED");
        if ((access & 0x0008) != 0) flags.add("STATIC");
        if ((access & 0x0010) != 0) flags.add("FINAL");
        if ((access & 0x0020) != 0) flags.add("SYNCHRONIZED");
        if ((access & 0x0040) != 0) flags.add("BRIDGE");
        if ((access & 0x0080) != 0) flags.add("VARARGS");
        if ((access & 0x0100) != 0) flags.add("NATIVE");
        if ((access & 0x0400) != 0) flags.add("ABSTRACT");
        if ((access & 0x0800) != 0) flags.add("STRICT");
        if ((access & 0x1000) != 0) flags.add("SYNTHETIC");
        return flags.toString();
    }

    private static String getAccessString(int access) {
        if ((access & 0x0001) != 0) return "public ";
        if ((access & 0x0002) != 0) return "private ";
        if ((access & 0x0004) != 0) return "protected ";
        return "";
    }

    private static int getArgsSize(String descriptor, int access) {
        int size = (access & 0x0008) != 0 ? 0 : 1; // 0 if static, 1 for 'this'
        int i = 1;
        while (descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            if (c == 'J' || c == 'D') {
                size += 2;
            } else if (c == '[') {
                while (descriptor.charAt(i) == '[') i++;
                if (descriptor.charAt(i) == 'L') {
                    while (descriptor.charAt(i) != ';') i++;
                }
                size += 1;
            } else if (c == 'L') {
                while (descriptor.charAt(i) != ';') i++;
                size += 1;
            } else {
                size += 1;
            }
            i++;
        }
        return size;
    }

    private static class ConsoleClassVisitor implements ClassVisitor {
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.println("Class: " + name);
            System.out.println("Super: " + superName);
            System.out.println("Flags: " + formatFlags(access));
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            System.out.printf("  Field: %s %s\n", descriptor, name);
            System.out.printf("    Flags: %s\n", formatFlags(access));
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.printf("\nMethod: %s%s%s\n", getAccessString(access), name, descriptor);
            System.out.printf("  Flags: %s\n", formatFlags(access));
            System.out.printf("  Args_Size: %d\n", getArgsSize(descriptor, access));
            return new ConsoleMethodVisitor();
        }

        @Override
        public void visitAttribute(String name, byte[] data) {
        }

        @Override
        public void visitEnd() {
        }
    }

    private static class ConsoleMethodVisitor implements MethodVisitor {
        private int currentPc = 0;

        @Override
        public void visitCode() {
            System.out.println("\n  Code:");
        }

        @Override
        public void visitInsn(int opcode) {
            System.out.printf("    %3d: %s\n", currentPc, getMnemonic(opcode));
            currentPc += 1;
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            System.out.printf("    %3d: %s %d\n", currentPc, getMnemonic(opcode), operand);
            currentPc += (opcode == 17 ? 3 : 2);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            System.out.printf("    %3d: %s %d\n", currentPc, getMnemonic(opcode), varIndex);
            currentPc += 2;
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            System.out.printf("    %3d: %s %s\n", currentPc, getMnemonic(opcode), type);
            currentPc += 3;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            System.out.printf("    %3d: %s %s.%s:%s\n", currentPc, getMnemonic(opcode), owner, name, descriptor);
            currentPc += 3;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            System.out.printf("    %3d: %s %s.%s:%s\n", currentPc, getMnemonic(opcode), owner, name, descriptor);
            currentPc += (opcode == 185 || opcode == 186 ? 5 : 3);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            System.out.printf("    %3d: %s %s\n", currentPc, getMnemonic(opcode), label);
            currentPc += 3;
        }

        @Override
        public void visitLabel(Label label) {
            System.out.printf("  %s:\n", label);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        }

        @Override
        public void visitLineNumber(int line, Label start) {
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            System.out.printf("\n  Stack: %d, Locals: %d\n", maxStack, maxLocals);
        }

        @Override
        public void visitAttribute(String name, byte[] data) {
            if (name.equals("LineNumberTable")) {
                System.out.println("  Attributes:\n    LineNumberTable:");
                parseLineNumberTable(data);
            } else if (name.equals("LocalVariableTable")) {
                System.out.println("    LocalVariableTable:");
                parseLocalVariableTable(data);
            }
        }

        private void parseLineNumberTable(byte[] data) {
            try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(data))) {
                int count = din.readUnsignedShort();
                for (int i = 0; i < count; i++) {
                    int startPc = din.readUnsignedShort();
                    int line = din.readUnsignedShort();
                    System.out.printf("      line %d: %d\n", line, startPc);
                }
            } catch (IOException ignored) {}
        }

        private void parseLocalVariableTable(byte[] data) {
            try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(data))) {
                // This is a placeholder since we don't have CP access here for names
                int count = din.readUnsignedShort();
                System.out.printf("      (Contains %d entries)\n", count);
            } catch (IOException ignored) {}
        }

        @Override
        public void visitEnd() {
            System.out.println("\n  Exception Table: \n    <empty>");
        }

        private String getMnemonic(int opcode) {
            // Simplified mnemonic mapping
            return switch (opcode) {
                case 0x00 -> "nop";
                case 0x01 -> "aconst_null";
                case 0x10 -> "bipush";
                case 0x11 -> "sipush";
                case 0x12 -> "ldc";
                case 0x19 -> "aload";
                case 0x2a -> "aload_0";
                case 0x2b -> "aload_1";
                case 0x2c -> "aload_2";
                case 0x2d -> "aload_3";
                case 0xb0 -> "areturn";
                case 0xb1 -> "return";
                case 0xb2 -> "getstatic";
                case 0xb4 -> "getfield";
                case 0xb5 -> "putfield";
                case 0xb6 -> "invokevirtual";
                case 0xb7 -> "invokespecial";
                case 0xb8 -> "invokestatic";
                case 0xbb -> "new";
                case 0xbf -> "athrow";
                case 0x9f -> "if_icmpeq";
                default -> "0x" + Integer.toHexString(opcode);
            };
        }
    }
}
