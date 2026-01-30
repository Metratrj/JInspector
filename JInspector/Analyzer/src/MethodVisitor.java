public interface MethodVisitor {
    void visitCode(byte[] opcodes);

    void visitAttribute(String name, byte[] data);

    void visitEnd();
}
