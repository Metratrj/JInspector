package xyz.metratrj.jbyteinspector.model;

/**
 * Interface for visiting a Java method.
 */
public interface MethodVisitor {
    /**
     * Visits the code of the method.
     */
    void visitCode();

    /**
     * Visits a zero-operand instruction.
     * 
     * @param opcode the instruction opcode
     */
    void visitInsn(int opcode);

    /**
     * Visits an instruction with a single integer operand.
     * 
     * @param opcode the instruction opcode
     * @param operand the instruction operand
     */
    void visitIntInsn(int opcode, int operand);

    /**
     * Visits a local variable instruction.
     * 
     * @param opcode the instruction opcode
     * @param varIndex the local variable index
     */
    void visitVarInsn(int opcode, int varIndex);

    /**
     * Visits a type instruction.
     * 
     * @param opcode the instruction opcode
     * @param type the internal name of the class or interface
     */
    void visitTypeInsn(int opcode, String type);

    /**
     * Visits a field instruction.
     * 
     * @param opcode the instruction opcode
     * @param owner the internal name of the field's owner class
     * @param name the field's name
     * @param descriptor the field's descriptor
     */
    void visitFieldInsn(int opcode, String owner, String name, String descriptor);

    /**
     * Visits a method instruction.
     * 
     * @param opcode the instruction opcode
     * @param owner the internal name of the method's owner class
     * @param name the method's name
     * @param descriptor the method's descriptor
     * @param isInterface whether the method's owner class is an interface
     */
    void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface);

    /**
     * Visits a jump instruction.
     * 
     * @param opcode the instruction opcode
     * @param label the operand of the instruction
     */
    void visitJumpInsn(int opcode, Label label);

    /**
     * Visits a label.
     * 
     * @param label the label to visit
     */
    void visitLabel(Label label);

    /**
     * Visits a stack map frame.
     * 
     * @param type the type of the frame
     * @param numLocal the number of local variables
     * @param local the local variable types
     * @param numStack the number of stack items
     * @param stack the stack item types
     */
    void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack);

    /**
     * Visits a line number information.
     * 
     * @param line the line number
     * @param start the first instruction of the line
     */
    void visitLineNumber(int line, Label start);

    /**
     * Visits the maximum stack size and local variable count of the method.
     * 
     * @param maxStack the maximum stack size
     * @param maxLocals the maximum local variable count
     */
    void visitMaxs(int maxStack, int maxLocals);

    /**
     * Visits a non-standard attribute of the method.
     * 
     * @param name the attribute name
     * @param data the raw attribute data
     */
    void visitAttribute(String name, byte[] data);

    /**
     * Visits the end of the method.
     */
    void visitEnd();
}
