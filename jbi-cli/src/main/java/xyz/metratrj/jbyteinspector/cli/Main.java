package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.io.ParallelLoader;
import xyz.metratrj.jbyteinspector.model.*;
import xyz.metratrj.jbyteinspector.parser.ClassReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
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

    private static class ConsoleClassVisitor implements ClassVisitor {
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.println("Class: " + name);
            System.out.println("Super: " + superName);
            System.out.println("Flags: 0x" + Integer.toHexString(access));
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            System.out.printf("  Field: %s %s (Flags: 0x%s)\n", descriptor, name, Integer.toHexString(access));
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.printf("\n  Method: %s %s (Flags: 0x%s)\n", name, descriptor, Integer.toHexString(access));
            return new ConsoleMethodVisitor();
        }

        @Override
        public void visitAttribute(String name, byte[] data) {
            System.out.printf("  Attribute: %s (length: %d)\n", name, data.length);
        }

        @Override
        public void visitEnd() {
        }
    }

    private static class ConsoleMethodVisitor implements MethodVisitor {
        @Override
        public void visitCode() {
            System.out.println("    Code:");
        }

        @Override
        public void visitInsn(int opcode) {
            System.out.printf("      %s\n", Integer.toHexString(opcode));
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            System.out.printf("      %s %d\n", Integer.toHexString(opcode), operand);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            System.out.printf("      %s var %d\n", Integer.toHexString(opcode), varIndex);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            System.out.printf("      %s %s\n", Integer.toHexString(opcode), type);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            System.out.printf("      %s %s.%s %s\n", Integer.toHexString(opcode), owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            System.out.printf("      %s %s.%s %s\n", Integer.toHexString(opcode), owner, name, descriptor);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            System.out.printf("      %s %s\n", Integer.toHexString(opcode), label);
        }

        @Override
        public void visitLabel(Label label) {
            System.out.printf("    %s:\n", label);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            System.out.printf("      line %d\n", line);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            System.out.printf("    MaxStack: %d, MaxLocals: %d\n", maxStack, maxLocals);
        }

        @Override
        public void visitAttribute(String name, byte[] data) {
            System.out.printf("    Method Attribute: %s (length: %d)\n", name, data.length);
        }

        @Override
        public void visitEnd() {
        }
    }
}