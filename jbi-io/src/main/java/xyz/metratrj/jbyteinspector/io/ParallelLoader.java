package xyz.metratrj.jbyteinspector.io;

import xyz.metratrj.jbyteinspector.model.DataRecord;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Parallel loader for class files and archives.
 */
public class ParallelLoader {

    private final ExecutorService executor;

    public ParallelLoader(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public ParallelLoader() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public List<DataRecord> load(Path root) throws IOException {
        List<DataRecord> results = Collections.synchronizedList(new ArrayList<>());
        List<Future<?>> futures = new ArrayList<>();

        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.toString().toLowerCase();
                if (fileName.endsWith(".class")) {
                    futures.add(executor.submit(() -> {
                        try {
                            byte[] data = Files.readAllBytes(file);
                            results.add(new DataRecord(file.toString(), data));
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read file: " + file, e);
                        }
                    }));
                } else if (fileName.endsWith(".jar") || fileName.endsWith(".zip")) {
                    futures.add(executor.submit(() -> {
                        try {
                            loadFromArchive(file, results);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read archive: " + file, e);
                        }
                    }));
                }
                return FileVisitResult.CONTINUE;
            }
        });

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Parallel loading failed", e);
            }
        }

        return results;
    }

    private void loadFromArchive(Path archive, List<DataRecord> results) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".class")) {
                    byte[] data = zis.readAllBytes();
                    results.add(new DataRecord(archive + "!" + entry.getName(), data));
                }
                zis.closeEntry();
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
