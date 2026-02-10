package xyz.metratrj.jbyteinspector.parser.classfile;

public record AttributeReport(String name,
                              int length,
                              byte[] data
) { }