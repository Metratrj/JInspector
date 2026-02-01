package xyz.metratrj.jbyteinspector.api;

import java.nio.file.Path;
import java.util.List;

public interface AnalysisService {
    /**
     * Analyzes all class files in the given path (directory or file).
     * @param inputPath Path to a .class file or directory containing .class files
     * @return List of reports, one per class file
     */
    List<ClassReport> analyze(Path inputPath);
}