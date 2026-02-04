package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.*;
import xyz.metratrj.jbyteinspector.parser.classfile.ClassFile;
import xyz.metratrj.jbyteinspector.parser.classfile.Opcodes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*if (args.length == 0) {
            System.out.println("Usage: JByteInspector <path-to-classes>");
            return;
        }*/

        //Path oPath  = Paths.get(args[0]);
        Path oPath = Paths.get("jbi-examples-1.0.0.jar");
        //Path oPath = Paths.get("/home/metratrj/sources/JInspector/JInspector/out/production/TestModule/xyz/metratrj");

        System.out.println("Inspecting: " + oPath.toAbsolutePath());

        AnalysisService   service = new JByteInspectorEngine();
        List<ClassReport> reports = service.analyze(oPath);

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
                    m.attributes().forEach(a -> {
                        System.out.printf("    %s\n", a.name());
                    });
                    displayMethodCode(m.code());
                }
            }
            System.out.println();
        }
    }

    private static void displayMethodCode(CodeReport code) {
        try {
            System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n",
                              code.maxStack(), code.maxLocals(), code.codeLength());
            System.out.println("      bytecode disassembly:");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
