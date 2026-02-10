package xyz.metratrj.jbyteinspector.model;

/**
 * Represents a raw class file or archive entry loaded into memory.
 * 
 * @param path Original file path or JAR entry path
 * @param data Raw binary content
 */
public record DataRecord(String path, byte[] data) {
}
