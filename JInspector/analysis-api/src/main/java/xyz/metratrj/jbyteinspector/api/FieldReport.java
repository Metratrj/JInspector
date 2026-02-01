package xyz.metratrj.jbyteinspector.api;

import java.util.Set;

public record FieldReport(
    String name,
    Set<String> flags
) {}
