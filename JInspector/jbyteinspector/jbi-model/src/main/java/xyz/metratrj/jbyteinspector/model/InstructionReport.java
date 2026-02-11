package xyz.metratrj.jbyteinspector.model;

import java.util.List;

public record InstructionReport(
        int pc,
        String mnemonic,
        List<String> operands,
        String resolvedComment
) { }
