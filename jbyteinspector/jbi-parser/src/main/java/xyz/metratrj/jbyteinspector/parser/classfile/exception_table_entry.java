package xyz.metratrj.jbyteinspector.parser.classfile;

public record exception_table_entry(int start_pc, int end_pc, int handler_pc, int catch_type) {
}
