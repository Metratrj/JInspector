package xyz.metratrj.jbyteinspector.cli;

import xyz.metratrj.jbyteinspector.api.AnalysisService;
import xyz.metratrj.jbyteinspector.core.JByteInspectorEngine;
import xyz.metratrj.jbyteinspector.model.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path oPath;
        if (args.length > 0) {
            oPath = Paths.get(args[0]);
        } else {
            oPath = Paths.get(".");
        }

        System.out.println("Inspecting: " + oPath.toAbsolutePath());

        AnalysisService service = new JByteInspectorEngine();
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
                    if (m.code() != null) {
                        System.out.printf("      max_stack: %d, max_locals: %d, code_length: %d\n",
                                m.code().maxStack(), m.code().maxLocals(), m.code().codeLength());
                        System.out.println("      bytecode disassembly:");
                        for (InstructionReport insn : m.code().instructions()) {
                            System.out.printf("        %04d: %s %s", insn.pc(), insn.mnemonic(), insn.operands());
                            if (!insn.resolvedComment().isEmpty()) {
                                System.out.print(" // " + insn.resolvedComment());
                            }
                            System.out.println();
                        }
                    }
                }
            }
            System.out.println();
        }
    }
}
