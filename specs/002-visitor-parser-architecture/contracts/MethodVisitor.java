package xyz.jinspector.model;

/**
 * Interface for visiting a Java method.
 */
public interface MethodVisitor {
    void visitCode();
    
    void visitInsn(int opcode);
    
    void visitIntInsn(int opcode, int operand);
    
    void visitVarInsn(int opcode, int varIndex);
    
    void visitTypeInsn(int opcode, String type);
    
    void visitFieldInsn(int opcode, String owner, String name, String descriptor);
    
    void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface);
    
    void visitJumpInsn(int opcode, Label label);
    
    void visitLabel(Label label);
    
    void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack);
    
    void visitLineNumber(int line, Label start);
    
    void visitMaxs(int maxStack, int maxLocals);
    
    void visitEnd();
}
