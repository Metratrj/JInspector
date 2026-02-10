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

class ClassReaderTest {

    @Test
    void testParseOwnClass() throws IOException {
        Path path = Paths.get("../jbi-parser/build/classes/java/main/xyz/metratrj/jbyteinspector/parser/ClassReader.class");
        if (!Files.exists(path)) {
            path = Paths.get("jbi-parser/build/classes/java/main/xyz/metratrj/jbyteinspector/parser/ClassReader.class");
        }
        
        // Ensure the class is compiled
        assertTrue(Files.exists(path), "Class file not found at " + path.toAbsolutePath());

        byte[] data = Files.readAllBytes(path);
        ClassReader reader = new ClassReader(data);
        
        List<String> methods = new ArrayList<>();
        reader.accept(new ClassVisitor() {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                assertEquals("xyz/metratrj/jbyteinspector/parser/ClassReader", name);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                methods.add(name);
                return null;
            }

            @Override
            public void visitAttribute(String name, byte[] data) {}

            @Override
            public void visitEnd() {}
        });

        assertTrue(methods.contains("<init>"));
        assertTrue(methods.contains("accept"));
    }
}
