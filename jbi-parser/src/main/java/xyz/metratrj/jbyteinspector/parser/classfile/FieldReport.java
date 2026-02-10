package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Set;

public record FieldReport(
        String name,
        Set<String> flags
) { }
