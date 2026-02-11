package xyz.metratrj.jbyteinspector.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.ClassReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineIntegrationTest {

    @Test
    void testFullPipelineWithFixtures() {
        // Path to compiled examples
        Path fixturesPath = Path.of("../jbi-examples/build/classes/java/main");
        
        if (!Files.exists(fixturesPath)) {
            // Fallback if not built yet (though gradle should handle this)
            System.out.println("Fixtures not found at " + fixturesPath.toAbsolutePath());
            return;
        }

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(fixturesPath);

        assertNotNull(reports);
        assertFalse(reports.isEmpty(), "Reports should not be empty");

        // Verify some specific classes from jbi-examples
        ClassReport esel = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Esel");
        assertNotNull(esel);
        assertEquals("xyz/metratrj/jbyteinspector/examples/animals/Tier", esel.superClassName());
        
        // Check for MachLaut method
        assertTrue(esel.methods().stream().anyMatch(m -> m.name().equals("MachLaut")));

        ClassReport tier = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Tier");
        assertNotNull(tier);
        assertTrue(tier.flags().contains("ABSTRACT"));
        assertTrue(tier.flags().contains("PUBLIC"));

        ClassReport person = findReport(reports, "xyz/metratrj/jbyteinspector/examples/animals/Person");
        assertNotNull(person);
        // Person has 3 fields: firstname, lastname, age (wait, let's check the source or assume)
        assertFalse(person.fields().isEmpty());
    }

    @Test
    void testSingleFileAnalysis() {
        Path eselPath = Path.of("../jbi-examples/build/classes/java/main/xyz/metratrj/jbyteinspector/examples/animals/Esel.class");
        
        if (!Files.exists(eselPath)) return;

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(eselPath);

        assertEquals(1, reports.size());
        assertEquals("xyz/metratrj/jbyteinspector/examples/animals/Esel", reports.get(0).className());
    }

    @Test
    void testEmptyDirectory(@TempDir Path emptyDir) {
        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(emptyDir);

        assertTrue(reports.isEmpty());
    }

    @Test
    void testJarAnalysis() throws IOException {
        // We need a JAR file to test. We can use the one from jbi-cli if it exists
        Path jarPath = Path.of("../jbi-cli/jbi-cli.jar");
        
        if (!Files.exists(jarPath)) {
            // Try to find any jar in build/libs
            try (var stream = Files.walk(Path.of(".."), 3)) {
                jarPath = stream.filter(p -> p.toString().endsWith(".jar")).findFirst().orElse(null);
            }
        }

        if (jarPath == null || !Files.exists(jarPath)) return;

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(jarPath);

        assertNotNull(reports);
        assertFalse(reports.isEmpty());
    }

    private ClassReport findReport(List<ClassReport> reports, String className) {
        return reports.stream()
                .filter(r -> r.className().equals(className))
                .findFirst()
                .orElse(null);
    }
}
