package xyz.metratrj.jbyteinspector.core;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void testFindFiles() throws IOException, URISyntaxException {
        var resource = getClass().getResource("/fixtures");
        assertNotNull(resource);
        Path root = Paths.get(resource.toURI());

        List<Path> classFiles = FileUtils.findFiles(root, p -> p.toString().endsWith(".class"));
        
        // Should find test.class and sub.class
        assertEquals(2, classFiles.size(), "Should find exactly 2 .class files");
        assertTrue(classFiles.stream().anyMatch(p -> p.getFileName().toString().equals("test.class")));
        assertTrue(classFiles.stream().anyMatch(p -> p.getFileName().toString().equals("sub.class")));
        assertFalse(classFiles.stream().anyMatch(p -> p.getFileName().toString().equals("test.txt")));
    }
}
