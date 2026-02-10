package xyz.jinspector.model;

/**
 * Interface for visiting a Java field.
 */
public interface FieldVisitor {
    void visitAttribute(String name, byte[] data);
    
    void visitEnd();
}
