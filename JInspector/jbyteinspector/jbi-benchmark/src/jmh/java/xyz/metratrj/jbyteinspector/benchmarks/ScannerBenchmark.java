package xyz.metratrj.jbyteinspector.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ScannerBenchmark {
    private Path            testPath;
    private SimpleScanner   simpleScanner;
    private ParallelScanner parallelScanner;

    @Setup
    public void setup() {
        testPath        = Path.of("../jbi-examples/build/classes/java/main/xyz/metratrj");
        simpleScanner   = new SimpleScanner();
        parallelScanner = new ParallelScanner();
    }

    @Benchmark
    public List<ClassScanner.ClassData> testSimpleScanner() throws Exception {
        return simpleScanner.scan(testPath);
    }

    @Benchmark
    public List<ClassScanner.ClassData> testParallelScanner() throws Exception {
        return parallelScanner.scan(testPath);
    }
}
