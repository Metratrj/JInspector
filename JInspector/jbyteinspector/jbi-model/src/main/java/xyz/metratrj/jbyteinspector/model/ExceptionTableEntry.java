package xyz.metratrj.jbyteinspector.model;

public record ExceptionTableEntry(
        int startPc,
        int endPc,
        int handlerPc,
        int catchType
) { }
