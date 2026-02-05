package xyz.metratrj.jbyteinspector.parser.classfile;

public record Instruction(int pc, int opcode, String mnemonic, Object[] operands) {
}
