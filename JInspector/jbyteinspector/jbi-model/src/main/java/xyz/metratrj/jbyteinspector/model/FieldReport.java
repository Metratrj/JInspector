package xyz.metratrj.jbyteinspector.model;

import java.util.Set;

public record FieldReport(
    String name,
    Set<String> flags
) {}
