package xyz.metratrj.jbyteinspector.model;

import java.util.Set;

public record MethodReport(
    String name,
    String descriptor,
    Set<String> flags
) {}
