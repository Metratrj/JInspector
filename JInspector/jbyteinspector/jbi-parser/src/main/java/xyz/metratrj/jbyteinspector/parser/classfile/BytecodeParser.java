package xyz.metratrj.jbyteinspector.parser.classfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BytecodeParser {

    public static String getMnemonic(int opcode) {
        // This is a simplified version, should ideally map all opcodes
        for (java.lang.reflect.Field field : ClassFile.class.getFields()) {
            try {
                if (field.getType() == int.class && field.getModifiers() == (java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.STATIC | java.lang.reflect.Modifier.FINAL)) {
                    if (field.getInt(null) == opcode && !field.getName().startsWith("ACC_") && !field.getName().startsWith("CONSTANT_") && !field.getName().equals("JAVA_MAGIC")) {
                        return field.getName();
                    }
                }
            } catch (IllegalAccessException e) {
                // ignore
            }
        }
        return "UNKNOWN_" + opcode;
    }

    public static List<Instruction> parse(byte[] code) {
        return parseTo(code);
    }

    private static List<Instruction> parseTo(byte[] code) {
        List<Instruction> instructions = new ArrayList<>();
        ByteBuffer        bb           = ByteBuffer.wrap(code);
        bb.order(ByteOrder.BIG_ENDIAN);

        while (bb.hasRemaining()) {
            int          pc       = bb.position();
            int          opcode   = bb.get() & 0xFF;
            String       mnemonic = getMnemonic(opcode);
            List<Object> operands = new ArrayList<>();

            switch (opcode) {
                case ClassFile.BIPUSH -> operands.add((int) bb.get());
                case ClassFile.SIPUSH -> operands.add((int) bb.getShort());
                case ClassFile.LDC -> operands.add(bb.get() & 0xFF);
                case ClassFile.LDC_W, ClassFile.LDC2_W, ClassFile.GETSTATIC, ClassFile.PUTSTATIC,
                     ClassFile.GETFIELD, ClassFile.PUTFIELD, ClassFile.INVOKEVIRTUAL,
                     ClassFile.INVOKESPECIAL, ClassFile.INVOKESTATIC, ClassFile.NEW,
                     ClassFile.ANEWARRAY, ClassFile.CHECKCAST,
                     ClassFile.INSTANCEOF -> operands.add(bb.getShort() & 0xFFFF);

                case ClassFile.IINC -> {
                    operands.add(bb.get() & 0xFF); // index
                    operands.add((int) bb.get());  // const

                }

                case ClassFile.IFEQ, ClassFile.IFNE, ClassFile.IFLT, ClassFile.IFGE,
                     ClassFile.IFGT, ClassFile.IFLE, ClassFile.IF_ICMPEQ, ClassFile.IF_ICMPNE,
                     ClassFile.IF_ICMPLT, ClassFile.IF_ICMPGE, ClassFile.IF_ICMPGT,
                     ClassFile.IF_ICMPLE, ClassFile.IF_ACMPEQ, ClassFile.IF_ACMPNE,
                     ClassFile.GOTO, ClassFile.JSR, ClassFile.IFNULL,
                     ClassFile.IFNONNULL -> operands.add(pc + bb.getShort());

                case ClassFile.GOTO_W, ClassFile.JSR_W -> operands.add(pc + bb.getInt());

                case ClassFile.INVOKEINTERFACE -> {
                    operands.add(bb.getShort() & 0xFFFF);
                    operands.add(bb.get() & 0xFF); // count
                    bb.get(); // zero
                }

                case ClassFile.INVOKEDYNAMIC -> {
                    operands.add(bb.getShort() & 0xFFFF);
                    bb.getShort(); // zero
                }

                case ClassFile.NEWARRAY -> operands.add(bb.get() & 0xFF);

                case ClassFile.MULTIANEWARRAY -> {
                    operands.add(bb.getShort() & 0xFFFF);
                    operands.add(bb.get() & 0xFF);
                }

                case ClassFile.TABLESWITCH -> {
                    int currentPos = bb.position();
                    int padding    = (4 - (currentPos % 4)) % 4;
                    for (int k = 0; k < padding; k++)
                         bb.get();
                    int defaultOffset = bb.getInt();
                    int low           = bb.getInt();
                    int high          = bb.getInt();
                    operands.add(pc + defaultOffset);
                    operands.add(low);
                    operands.add(high);
                    int numPairs = high - low + 1;
                    for (int k = 0; k < numPairs; k++) {
                        operands.add(pc + bb.getInt());
                    }
                }

                case ClassFile.LOOKUPSWITCH -> {
                    int currentPos = bb.position();
                    int padding    = (4 - (currentPos % 4)) % 4;
                    for (int k = 0; k < padding; k++)
                         bb.get();
                    int defaultOffset = bb.getInt();
                    int npairs        = bb.getInt();
                    operands.add(pc + defaultOffset);
                    operands.add(npairs);
                    for (int k = 0; k < npairs; k++) {
                        operands.add(bb.getInt()); // match
                        operands.add(pc + bb.getInt()); // offset
                    }
                }

                case ClassFile.ILOAD, ClassFile.LLOAD, ClassFile.FLOAD, ClassFile.DLOAD, ClassFile.ALOAD,
                     ClassFile.ISTORE, ClassFile.LSTORE, ClassFile.FSTORE, ClassFile.DSTORE, ClassFile.ASTORE,
                     ClassFile.RET -> operands.add(bb.get() & 0xFF);

                case ClassFile.WIDE -> {
                    int extendedOpcode = bb.get() & 0xFF;
                    operands.add(extendedOpcode);
                    if (extendedOpcode == ClassFile.IINC) {
                        operands.add(bb.getShort() & 0xFFFF);
                        operands.add((int) bb.getShort());
                    }
                    else {
                        operands.add(bb.getShort() & 0xFFFF);
                    }
                }

                default -> {
                    // Simple instructions with no operands already handled by just reading opcode
                }
            }
            instructions.add(new Instruction(pc, opcode, mnemonic, operands));
        }
        return instructions;
    }

    public record Instruction(int pc, int opcode, String mnemonic, List<Object> operands) {
        @Override
        public String toString() {
            return String.format("%04d: %s %s", pc, mnemonic, operands);
        }
    }
}

