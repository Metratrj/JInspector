package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Set;

public record MethodReport(
        String name,
        String descriptor,
        Set<String> flags,
        Set<AttributeReport> attributes,
        CodeReport code
) { }
