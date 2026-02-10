package xyz.metratrj.jbyteinspector.model;

/**
 * Interface for visiting a Java class.
 */
public interface ClassVisitor {
    /**
     * Visits the header of the class.
     * 
     * @param version the class version
     * @param access the class's access flags
     * @param name the internal name of the class
     * @param signature the signature of the class, may be {@code null}
     * @param superName the internal name of the super class, may be {@code null}
     * @param interfaces the internal names of the class's interfaces, may be {@code null}
     */
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces);

    /**
     * Visits a field of the class.
     * 
     * @param access the field's access flags
     * @param name the field's name
     * @param descriptor the field's descriptor
     * @param signature the field's signature, may be {@code null}
     * @param value the field's constant value, may be {@code null}
     * @return a visitor to visit field attributes, or {@code null}
     */
    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value);

    /**
     * Visits a method of the class.
     * 
     * @param access the method's access flags
     * @param name the method's name
     * @param descriptor the method's descriptor
     * @param signature the method's signature, may be {@code null}
     * @param exceptions the internal names of the method's exception classes, may be {@code null}
     * @return a visitor to visit method attributes and code, or {@code null}
     */
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions);

    /**
     * Visits a non-standard attribute of the class.
     * 
     * @param name the attribute name
     * @param data the raw attribute data
     */
    void visitAttribute(String name, byte[] data);

    /**
     * Visits the end of the class.
     */
    void visitEnd();
}
