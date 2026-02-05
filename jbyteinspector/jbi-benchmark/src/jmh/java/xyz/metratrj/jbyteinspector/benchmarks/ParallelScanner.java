package xyz.metratrj.jbyteinspector.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelScanner implements ClassScanner {
    @Override
    public List<ClassData> scan(Path rootPath) throws Exception {
        try (Stream<Path> walk = Files.walk(rootPath)) {
            return walk.parallel() // Take usage of common ForkJoinPools
                       .filter(p -> p.toString().endsWith(".class"))
                       .map(p -> {
                           try {
                               return new ClassData(p.getFileName().toString(), Files.readAllBytes(p));
                           } catch (IOException e) {
                               return null;
                           }
                       })
                       .filter(Objects::nonNull)
                       .collect(Collectors.toList());
        }
    }
}
