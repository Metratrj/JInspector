package xyz.metratrj.jbyteinspector.parser.classfile;

public record ExceptionTableEntry(
        int startPc,
        int endPc,
        int handlerPc,
        int catchType
) {
}
