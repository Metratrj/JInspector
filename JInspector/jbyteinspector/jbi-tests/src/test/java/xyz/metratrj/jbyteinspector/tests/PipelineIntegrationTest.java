package xyz.metratrj.jbyteinspector.tests;

import org.junit.jupiter.api.Test;
import xyz.metratrj.jbyteinspector.model.AnalysisService;
import xyz.metratrj.jbyteinspector.model.ClassReport;
import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineIntegrationTest {

    @Test
    void testFullPipelineWithAnimalExamples() throws URISyntaxException {
        // 1. Setup - locate the fixtures
        var resource = getClass().getResource("/fixtures/animals");
        assertNotNull(resource, "Fixtures directory not found");
        Path fixturesPath = Paths.get(resource.toURI());

        // 2. Execution - run the engine
        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(fixturesPath);

        // 3. Verification
        assertNotNull(reports);
        assertEquals(5, reports.size(), "Should have analyzed 5 animal classes");

        // Verify Esel
        ClassReport esel = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Esel");
        assertNotNull(esel);
        assertEquals("xyz/metratrj/jbyteinspector/examples/animals/Tier", esel.superClassName());
        assertTrue(esel.flags().contains("PUBLIC"));
        assertTrue(esel.methods().stream().anyMatch(m -> m.name().equals("MachLaut")));

        // Verify Tier (Abstract Class)
        ClassReport tier = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Tier");
        assertNotNull(tier);
        assertTrue(tier.flags().contains("ABSTRACT"));
        assertTrue(tier.fields().stream().anyMatch(f -> f.name().equals("name")));

        // Verify Person
        ClassReport person = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Person");
        assertNotNull(person);
        assertTrue(person.fields().stream().anyMatch(f -> f.name().equals("haustiere")));
    }

    @Test
    void testSingleFileAnalysis() throws URISyntaxException {
        var resource = getClass().getResource("/fixtures/animals/Esel.class");
        assertNotNull(resource);
        Path eselPath = Paths.get(resource.toURI());

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(eselPath);

        assertEquals(1, reports.size());
        assertEquals("xyz/metratrj/jbyteinspector/examples/animals/Esel", reports.get(0).className());
    }

    @Test
    void testEmptyDirectoryAnalysis() throws IOException {
        Path emptyDir = Files.createTempDirectory("empty-classes");
        try {
            AnalysisService   service = new JByteInspectorEngine();
            List<ClassReport> reports = service.analyze(emptyDir);
            assertTrue(reports.isEmpty(), "Should return empty list for empty directory");
        } finally {
            Files.delete(emptyDir);
        }
    }

    @Test
    void testJarAnalysis() throws URISyntaxException {
        Path jarPath = Paths.get("jbyteinspector/jbi-examples/build/libs/jbi-examples-0.1.0-SNAPSHOT.jar");
        // Note: The path might be relative to the 'tests' module directory if running via gradle
        if (!Files.exists(jarPath)) {
             jarPath = Paths.get("../jbi-examples/build/libs/jbi-examples-0.1.0-SNAPSHOT.jar");
        }
        if (!Files.exists(jarPath)) {
            var resource = getClass().getResource("/fixtures.jar/jbi-examples-1.0.0.jar");
            assertNotNull(resource, "JAR fixture resource not found");
            jarPath = Paths.get(resource.toURI());
        }
        assertTrue(Files.exists(jarPath), "JAR fixture not found at " + jarPath.toAbsolutePath());

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(jarPath);

        assertEquals(6, reports.size(), "Should have analyzed 6 classes inside the JAR");
        assertTrue(reports.stream().anyMatch(r -> r.className().contains("Esel")));
    }

    private ClassReport findReport(List<ClassReport> reports, String className) {
        return reports.stream()
                      .filter(r -> r.className().equals(className))
                      .findFirst()
                      .orElse(null);
    }
}
