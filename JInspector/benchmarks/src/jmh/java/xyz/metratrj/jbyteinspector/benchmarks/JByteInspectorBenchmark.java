package xyz.metratrj.jbyteinspector.benchmarks;

import org.openjdk.jmh.annotations.*;
import xyz.metratrj.jbyteinspector.analysis.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.api.ClassReport;
import xyz.metratrj.jbyteinspector.parser.classfile.ClassFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JByteInspectorBenchmark {

    private Path fixturePath;
    private AnalysisService engine;

    @Setup
    public void setup() throws IOException {
        var inputStream = getClass().getResourceAsStream("/fixtures/Person.class");
        if (inputStream == null) {
            throw new RuntimeException("Fixture not found!");
        }
        
        // Copy to a real file in a temporary directory
        fixturePath = Files.createTempFile("Person", ".class");
        Files.copy(inputStream, fixturePath, StandardCopyOption.REPLACE_EXISTING);
        
        engine = new JByteInspectorEngine();
    }

    @TearDown
    public void tearDown() throws IOException {
        Files.deleteIfExists(fixturePath);
    }

    @Benchmark
    public ClassFile benchmarkRawParser() throws IOException {
        return ClassFile.parse(fixturePath);
    }

    @Benchmark
    public List<ClassReport> benchmarkFullEngine() {
        return engine.analyze(fixturePath);
    }
}
