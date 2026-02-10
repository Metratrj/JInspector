package xyz.jinspector.model;

/**
 * Interface for visiting a Java class.
 */
public interface ClassVisitor {
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces);
    
    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value);
    
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions);
    
    void visitAttribute(String name, byte[] data);
    
    void visitEnd();
}
