package xyz.metratrj.jbyteinspector.benchmarks;

import java.nio.file.Path;
import java.util.List;

public interface ClassScanner {
    List<ClassData> scan(Path rootPath) throws Exception;

    record ClassData(String name, byte[] content) { }
}
