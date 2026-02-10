package xyz.metratrj.jbyteinspector.cli;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {
    public static List<FileData> readFilesFromDirectory(Path directoryPath, boolean recursive) {
        try (Stream<Path> walk = Files.walk(directoryPath, recursive ? Integer.MAX_VALUE : 1, FileVisitOption.FOLLOW_LINKS)){
            return walk.filter(p -> p.endsWith(".class"))
                       .filter(path -> !path.endsWith("module-info.class"))
                       .filter(path -> !path.endsWith("package-info.class"))
                    .map(path -> {
                        try {
                            return new FileData()
                        }
                    })
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
