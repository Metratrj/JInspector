package xyz.metratrj.jbyteinspector.core;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileUtils {
    public static List<Path> findFiles(Path root, Predicate<Path> filter) throws IOException {
        List<Path> result = new ArrayList<>();
        if (!Files.exists(root)) return result;
        
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (filter.test(file)) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // Log or ignore access errors
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }
}
