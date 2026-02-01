package xyz.metratrj.jbyteinspector.api;

import java.util.Set;

public record MethodReport(
    String name,
    String descriptor,
    Set<String> flags
) {}
