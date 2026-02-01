package xyz.metratrj.jbyteinspector.api;

import java.util.List;
import java.util.Set;

public record ClassReport(
    String className,
    String superClassName,
    Set<String> flags,
    List<MethodReport> methods,
    List<FieldReport> fields
) {}
