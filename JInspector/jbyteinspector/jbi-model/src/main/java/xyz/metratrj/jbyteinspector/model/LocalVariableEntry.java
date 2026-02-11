package xyz.metratrj.jbyteinspector.model;

public record LocalVariableEntry(
        int startPc,
        int length,
        String name,
        String descriptor,
        int index
) {
}
