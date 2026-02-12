package xyz.metratrj.jbyteinspector.parser.classfile;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ClassFileTest {

    @Test
    void testParseExampleClass() throws IOException, URISyntaxException {
        // Load the fixture
        var resource = getClass().getResource("/fixtures/Example.class");
        assertNotNull(resource, "Test fixture Example.class not found");
        Path path = Paths.get(resource.toURI());

        // Parse
        ClassFile cf = ClassFile.parse(path);

        // Verify basic properties
        assertEquals(0xCAFEBABE, cf.getMagic(), "Magic number should be 0xCAFEBABE");
        assertTrue(cf.getMajorVersion() >= 45, "Major version should be at least 45");
        
        // Verify we found some members
        assertTrue(cf.getMethodsCount() > 0, "Should have at least one method (constructor)");
        
        // Verify constant pool is populated
        assertTrue(cf.getConstantPoolCount() > 0, "Constant pool should not be empty");
        
        // Check for specific class name in constant pool
        boolean foundClassName = false;
        for (int i = 1; i < cf.getConstantPoolCount(); i++) {
            var item = cf.getConstantPoolItem(i);
            if (item instanceof CONSTANT_Utf8_info utf8) {
                if (utf8.getValue().contains("Example")) {
                    foundClassName = true;
                    break;
                }
            }
        }
        assertTrue(foundClassName, "Constant pool should contain 'Example'");
    }

    @Test
    void testParseInvalidFile() {
        Path fakePath = Paths.get("non_existent_file.class");
        assertThrows(IOException.class, () -> ClassFile.parse(fakePath));
    }
}
