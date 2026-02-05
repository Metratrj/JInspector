package xyz.metratrj.jbyteinspector.parser.classfile;

public record CodeReport(
        int maxStack,
        int maxLocals,
        int codeLength,
        byte[] code,
        int exceptionTableLength,
        ExceptionTableEntry[] exceptionTable,
        int attributesCount,
        AttributeReport[] attributes
) {
}
