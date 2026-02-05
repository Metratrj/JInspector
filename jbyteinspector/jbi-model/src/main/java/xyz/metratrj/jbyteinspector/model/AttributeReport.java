package xyz.metratrj.jbyteinspector.model;

public record AttributeReport(String name,
                              int length,
                              byte[] data
) { }