package xyz.metratrj.jbyteinspector.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.metratrj.jbyteinspector.model.DataRecord;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ParallelLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadDirectoryAndJar() throws IOException {
        // Create a dummy .class file in directory
        Path classFile = tempDir.resolve("Test.class");
        Files.write(classFile, new byte[]{1, 2, 3, 4});

        // Create a dummy .jar file
        Path jarFile = tempDir.resolve("lib.jar");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jarFile.toFile()))) {
            ZipEntry entry = new ZipEntry("Internal.class");
            zos.putNextEntry(entry);
            zos.write(new byte[]{5, 6, 7, 8});
            zos.closeEntry();
        }

        ParallelLoader loader = new ParallelLoader();
        try {
            List<DataRecord> records = loader.load(tempDir);

            assertEquals(2, records.size());
            
            boolean foundClass = false;
            boolean foundJarEntry = false;

            for (DataRecord record : records) {
                if (record.path().endsWith("Test.class")) {
                    assertArrayEquals(new byte[]{1, 2, 3, 4}, record.data());
                    foundClass = true;
                } else if (record.path().contains("lib.jar!") && record.path().endsWith("Internal.class")) {
                    assertArrayEquals(new byte[]{5, 6, 7, 8}, record.data());
                    foundJarEntry = true;
                }
            }

            assertTrue(foundClass, "Test.class not found");
            assertTrue(foundJarEntry, "Internal.class in lib.jar not found");
        } finally {
            loader.shutdown();
        }
    }
}
