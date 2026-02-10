package xyz.metratrj.jbyteinspector.model;

/**
 * Interface for visiting a Java field.
 */
public interface FieldVisitor {
    /**
     * Visits a non-standard attribute of the field.
     * 
     * @param name the attribute name
     * @param data the raw attribute data
     */
    void visitAttribute(String name, byte[] data);

    /**
     * Visits the end of the field.
     */
    void visitEnd();
}
