package xyz.metratrj.jbyteinspector.parser.classfile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Code_attribute extends attribute_info {
    private final int maxStack;
    private final int maxLocals;
    private final byte[] code;
    private final List<ExceptionTableEntry> exceptionTable;
    private final List<attribute_info> attributes;

    public record ExceptionTableEntry(int startPc, int endPc, int handlerPc, int catchType) {}

    public Code_attribute(int attribute_name_index, int attribute_length, byte[] info) throws IOException {
        super(attribute_name_index, attribute_length, info);
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(info))) {
            this.maxStack = in.readUnsignedShort();
            this.maxLocals = in.readUnsignedShort();
            int codeLength = in.readInt();
            this.code = new byte[codeLength];
            in.readFully(this.code);
            
            int exceptionTableLength = in.readUnsignedShort();
            this.exceptionTable = new ArrayList<>(exceptionTableLength);
            for (int i = 0; i < exceptionTableLength; i++) {
                exceptionTable.add(new ExceptionTableEntry(
                    in.readUnsignedShort(),
                    in.readUnsignedShort(),
                    in.readUnsignedShort(),
                    in.readUnsignedShort()
                ));
            }
            
            int attributesCount = in.readUnsignedShort();
            this.attributes = new ArrayList<>(attributesCount);
            for (int i = 0; i < attributesCount; i++) {
                int nameIdx = in.readUnsignedShort();
                int len = in.readInt();
                byte[] data = new byte[len];
                in.readFully(data);
                this.attributes.add(new attribute_info(nameIdx, len, data));
            }
        }
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public byte[] getCode() {
        return code;
    }

    public List<ExceptionTableEntry> getExceptionTable() {
        return exceptionTable;
    }

    public List<attribute_info> getAttributes() {
        return attributes;
    }
}
