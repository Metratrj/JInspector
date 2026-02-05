package xyz.metratrj.jbyteinspector.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class FileUtils {
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());
    public static List<Path> findFiles(Path root, Predicate<Path> filter) throws IOException {
        LOGGER.info("Starting recursive file search in: " + root);
        List<Path> result = new ArrayList<>();
        if (!Files.exists(root)) {
            LOGGER.warning("Path does not exist: " + root);
            return result;
        }

        if (Files.isRegularFile(root)) {
            if (filter.test(root)) {
                LOGGER.info("Found file: " + root);
                result.add(root);
            }
            return result;
        }

        Files.walkFileTree(root, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (filter.test(file)) {
                    LOGGER.info("Found file: " + root);
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                LOGGER.warning("Failed to visit file: " + file + " (" + exc.getMessage() + ")");
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }
}
