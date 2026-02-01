package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.analysis.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.api.ClassReport;
import xyz.metratrj.jbyteinspector.api.FieldReport;
import xyz.metratrj.jbyteinspector.api.MethodReport;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: JByteInspector <path-to-classes>");
            return;
        }

        Path path = Paths.get(args[0]);
        System.out.println("Inspecting: " + path.toAbsolutePath());

        AnalysisService service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(path);

        for (ClassReport report : reports) {
            System.out.println("--------------------------------------------------");
            System.out.println("Class: " + report.className());
            System.out.println("Super: " + report.superClassName());
            System.out.println("Flags: " + report.flags());
            
            if (!report.fields().isEmpty()) {
                System.out.println("\nFields:");
                for (FieldReport f : report.fields()) {
                    System.out.printf("  %s %s\n", f.flags(), f.name());
                }
            }
            
            if (!report.methods().isEmpty()) {
                System.out.println("\nMethods:");
                for (MethodReport m : report.methods()) {
                    System.out.printf("  %s %s %s\n", m.flags(), m.name(), m.descriptor());
                }
            }
            System.out.println();
        }
    }
}
