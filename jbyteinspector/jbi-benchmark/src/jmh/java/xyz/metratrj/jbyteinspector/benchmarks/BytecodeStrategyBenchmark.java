package xyz.metratrj.jbyteinspector.benchmarks;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BytecodeStrategyBenchmark {

    //    @Param({ "small-project", "large-project" })
    //    public String projectSize; // Teste mit verschiedenen Verzeichnisgrößen

    private Path rootPath;

    @Setup
    public void setup() {
        rootPath = Path.of("../../");
    }

    // STRATEGIE 1: Alles in einem Rutsch (Parallel Stream direkt auf Files.walk)
    @Benchmark
    public List<byte[]> directParallelWalkAndRead() throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            return paths.parallel()
                        .filter(p -> p.toString().endsWith(".class"))
                        .map(this::readBytesSafe)
                        .filter(Objects::nonNull)
                        .toList();
        }
    }

    // STRATEGIE 2: Erst Pfade sammeln (sequenziell), dann sequenziell lesen
    @Benchmark
    public List<byte[]> collectPathsThenSequentialRead() throws IOException {
        List<Path> paths = collectPathsSequentially();
        return paths.stream()
                    .map(this::readBytesSafe)
                    .toList();
    }

    // STRATEGIE 3: Erst Pfade sammeln (sequenziell), dann parallel lesen (Virtual Threads)
    @Benchmark
    public List<byte[]> collectPathsThenVirtualThreadRead() throws IOException {
        List<Path> paths = collectPathsSequentially();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = paths.stream()
                               .map(p -> executor.submit(() -> Files.readAllBytes(p)))
                               .toList();
            return futures.stream().map(this::getFutureSafe).filter(Objects::nonNull).toList();
        }
    }

    // STRATEGIE 4: Erst Pfade sammeln (sequenziell), dann Parallel Stream
    @Benchmark
    public List<byte[]> collectPathsThenParallelStreamRead() throws IOException {
        List<Path> paths = collectPathsSequentially();
        return paths.parallelStream()
                    .map(this::readBytesSafe)
                    .toList();
    }

    private List<Path> collectPathsSequentially() throws IOException {
        try (Stream<Path> s = Files.walk(rootPath)) {
            return s.filter(p -> p.toString().endsWith(".class")).toList();
        }
    }

    private List<Path> collectPathsParallel() throws IOException {
        try (Stream<Path> s = Files.walk(rootPath)) {
            return s.filter(p -> p.toString().endsWith(".class")).parallel().toList();
        }
    }

    private byte[] readBytesSafe(Path p) {
        try {
            return Files.readAllBytes(p);
        } catch (IOException e) {
            return null;
        }
    }

    private byte[] getFutureSafe(Future<byte[]> f) {
        try {
            return f.get();
        } catch (Exception e) {
            return null;
        }
    }
}