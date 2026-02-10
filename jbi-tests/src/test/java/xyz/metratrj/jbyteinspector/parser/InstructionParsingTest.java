package xyz.metratrj.jbyteinspector.parser;

import org.junit.jupiter.api.Test;
import xyz.metratrj.jbyteinspector.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstructionParsingTest {

    @Test
    void testParseMethodInstructions() throws IOException {
        Path path = Paths.get("../jbi-parser/build/classes/java/main/xyz/metratrj/jbyteinspector/parser/ClassReader.class");
        if (!Files.exists(path)) {
            path = Paths.get("jbi-parser/build/classes/java/main/xyz/metratrj/jbyteinspector/parser/ClassReader.class");
        }
        
        byte[] data = Files.readAllBytes(path);
        ClassReader reader = new ClassReader(data);
        
        List<Integer> opcodes = new ArrayList<>();
        reader.accept(new ClassVisitor() {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {}

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (name.equals("accept")) {
                    return new MethodVisitor() {
                        @Override
                        public void visitCode() {}

                        @Override
                        public void visitInsn(int opcode) {
                            opcodes.add(opcode);
                        }

                        @Override
                        public void visitIntInsn(int opcode, int operand) {}

                        @Override
                        public void visitVarInsn(int opcode, int varIndex) {}

                        @Override
                        public void visitTypeInsn(int opcode, String type) {}

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {}

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {}

                        @Override
                        public void visitJumpInsn(int opcode, Label label) {}

                        @Override
                        public void visitLabel(Label label) {}

                        @Override
                        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {}

                        @Override
                        public void visitLineNumber(int line, Label start) {}

                        @Override
                        public void visitMaxs(int maxStack, int maxLocals) {}

                        @Override
                        public void visitAttribute(String name, byte[] data) {}

                        @Override
                        public void visitEnd() {}
                    };
                }
                return null;
            }

            @Override
            public void visitAttribute(String name, byte[] data) {}

            @Override
            public void visitEnd() {}
        });

        assertFalse(opcodes.isEmpty(), "No opcodes found in accept method");
    }
}
