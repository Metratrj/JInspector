package xyz.metratrj.jbyteinspector.parser;

import xyz.metratrj.jbyteinspector.model.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses a Java class file and drives a ClassVisitor.
 */
public class ClassReader {
    private final byte[] b;
    private final ConstantPool cp;
    private final int access;
    private final String name;
    private final String superName;
    private final String[] interfaces;
    private final int headerOffset;

    public ClassReader(DataRecord record) throws IOException {
        this(record.data());
    }

    public ClassReader(byte[] b) throws IOException {
        this.b = b;
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(b));
        if (din.readInt() != 0xCAFEBABE) {
            throw new BytecodeException("Invalid magic number");
        }
        int minor = din.readUnsignedShort();
        int major = din.readUnsignedShort();
        
        this.cp = new ConstantPool(din);
        
        this.access = din.readUnsignedShort();
        this.name = cp.getClassName(din.readUnsignedShort());
        int superIdx = din.readUnsignedShort();
        this.superName = superIdx == 0 ? null : cp.getClassName(superIdx);
        
        int itfCount = din.readUnsignedShort();
        this.interfaces = new String[itfCount];
        for (int i = 0; i < itfCount; i++) {
            this.interfaces[i] = cp.getClassName(din.readUnsignedShort());
        }
        
        this.headerOffset = b.length - din.available();
    }

    public void accept(ClassVisitor cv) {
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(b, headerOffset, b.length - headerOffset));
            
            cv.visit(0, access, name, null, superName, interfaces);

            int fieldCount = din.readUnsignedShort();
            for (int i = 0; i < fieldCount; i++) {
                parseField(din, cv);
            }

            int methodCount = din.readUnsignedShort();
            for (int i = 0; i < methodCount; i++) {
                parseMethod(din, cv);
            }

            cv.visitEnd();
        } catch (IOException e) {
            throw new BytecodeException("Failed to parse class", e);
        }
    }

    private void parseField(DataInputStream din, ClassVisitor cv) throws IOException {
        int access = din.readUnsignedShort();
        String name = cp.getUtf8(din.readUnsignedShort());
        String descriptor = cp.getUtf8(din.readUnsignedShort());
        
        FieldVisitor fv = cv.visitField(access, name, descriptor, null, null);
        
        int attrCount = din.readUnsignedShort();
        for (int i = 0; i < attrCount; i++) {
            String attrName = cp.getUtf8(din.readUnsignedShort());
            int attrLen = din.readInt();
            byte[] attrData = new byte[attrLen];
            din.readFully(attrData);
            if (fv != null) {
                fv.visitAttribute(attrName, attrData);
            }
        }
        if (fv != null) {
            fv.visitEnd();
        }
    }

    private void parseMethod(DataInputStream din, ClassVisitor cv) throws IOException {
        int access = din.readUnsignedShort();
        String name = cp.getUtf8(din.readUnsignedShort());
        String descriptor = cp.getUtf8(din.readUnsignedShort());
        
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, null, null);
        
        int attrCount = din.readUnsignedShort();
        // Store attributes for lazy parsing
        Map<String, byte[]> attributes = new HashMap<>();
        for (int i = 0; i < attrCount; i++) {
            String attrName = cp.getUtf8(din.readUnsignedShort());
            int attrLen = din.readInt();
            byte[] attrData = new byte[attrLen];
            din.readFully(attrData);
            attributes.put(attrName, attrData);
        }

        if (mv != null) {
            byte[] code = attributes.get("Code");
            if (code != null) {
                parseCode(code, mv);
            }
            // Visit other attributes
            attributes.forEach((aname, adata) -> {
                if (!aname.equals("Code")) {
                    mv.visitAttribute(aname, adata);
                }
            });
            mv.visitEnd();
        }
    }

    private void parseCode(byte[] codeAttr, MethodVisitor mv) throws IOException {
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(codeAttr));
        int maxStack = din.readUnsignedShort();
        int maxLocals = din.readUnsignedShort();
        int codeLength = din.readInt();
        byte[] code = new byte[codeLength];
        din.readFully(code);
        
        // Exception handlers
        int exceptionCount = din.readUnsignedShort();
        for (int i = 0; i < exceptionCount; i++) {
            din.readUnsignedShort(); // start
            din.readUnsignedShort(); // end
            din.readUnsignedShort(); // handler
            din.readUnsignedShort(); // type
        }
        
        // Attributes
        int attrCount = din.readUnsignedShort();
        for (int i = 0; i < attrCount; i++) {
            String name = cp.getUtf8(din.readUnsignedShort());
            int len = din.readInt();
            byte[] data = new byte[len];
            din.readFully(data);
            mv.visitAttribute(name, data);
        }

        mv.visitCode();
        
        // Label Identification Pass
        Map<Integer, Label> labels = new HashMap<>();
        // In a real implementation, we would scan bytecode and exception table here.
        // For now, we simulate the sequential instruction loop.
        
        for (int i = 0; i < codeLength; ) {
            int opcode = code[i] & 0xFF;
            // visitLabel if any
            mv.visitInsn(opcode);
            i += getInsnSize(opcode, code, i);
        }
        
        mv.visitMaxs(maxStack, maxLocals);
    }

    private int getInsnSize(int opcode, byte[] code, int offset) {
        // Simplified instruction size mapping
        switch (opcode) {
            case 0: // nop
            case 1: // aconst_null
            case 2: case 3: case 4: case 5: case 6: case 7: case 8: // iconst
            case 42: case 43: case 44: case 45: // aload
            case 172: case 176: case 177: // return
                return 1;
            case 16: // bipush
            case 18: // ldc
            case 21: // iload
            case 25: // aload
            case 54: // istore
            case 58: // astore
                return 2;
            case 17: // sipush
            case 19: // ldc_w
            case 20: // ldc2_w
            case 132: // iinc
            case 153: case 154: case 155: case 156: case 157: case 158: // if
            case 159: case 160: case 161: case 162: case 163: case 164: // if_icmp
            case 165: case 166: // if_acmp
            case 167: // goto
            case 168: // jsr
            case 180: case 181: // getfield/putfield
            case 182: case 183: case 184: // invoke
            case 187: // new
            case 192: // checkcast
            case 193: // instanceof
                return 3;
            case 185: // invokeinterface
            case 186: // invokedynamic
            case 200: // goto_w
                return 5;
            default:
                return 1; // Default to 1 for safety in this POC
        }
    }
}