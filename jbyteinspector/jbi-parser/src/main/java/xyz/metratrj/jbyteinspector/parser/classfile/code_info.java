package xyz.metratrj.jbyteinspector.parser.classfile;


import java.nio.ByteBuffer;

public class code_info extends attribute_info {
    int                     maxStack;
    int                     maxLocals;
    int                     codeLength;
    byte[]                  code;
    int                     exceptionTableLength;
    exception_table_entry[] exceptionTable;
    int                     attributesCount;
    attribute_info[]        attributeReports;

    public code_info(int attribute_name_index, int attribute_length, byte[] info, int maxStack, int maxLocals,
                     int codeLength, byte[] code, int exceptionTableLength, exception_table_entry[] exceptionTable,
                     int attributesCount, attribute_info[] attributeReports) {
        super(attribute_name_index, attribute_length, info);
        this.maxStack             = maxStack;
        this.maxLocals            = maxLocals;
        this.codeLength           = codeLength;
        this.code                 = code;
        this.exceptionTableLength = exceptionTableLength;
        this.exceptionTable       = exceptionTable;
        this.attributesCount      = attributesCount;
        this.attributeReports     = attributeReports;
    }

    public static code_info parse(attribute_info info) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(info.info());
        byteBuffer.order(java.nio.ByteOrder.BIG_ENDIAN);

        int    maxStack   = byteBuffer.getShort();
        int    maxLocals  = byteBuffer.getShort();
        int    codeLength = byteBuffer.getInt();
        byte[] code       = new byte[codeLength];
        byteBuffer.get(code);

        int                     exceptionTableLength = byteBuffer.getShort();
        exception_table_entry[] exceptionTable       = new exception_table_entry[exceptionTableLength];
        for (int i = 0; i < exceptionTableLength; i++) {
            int start_pc   = byteBuffer.getShort();
            int end_pc     = byteBuffer.getShort();
            int handler_pc = byteBuffer.getShort();
            int catch_type = byteBuffer.getShort();
            exceptionTable[i] = new exception_table_entry(start_pc, end_pc, handler_pc, catch_type);
        }

        int              attributesCount  = byteBuffer.getShort();
        attribute_info[] attributeReports = new attribute_info[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            int    attribute_name_index = byteBuffer.getShort();
            int    attribute_length     = byteBuffer.getInt();
            byte[] data                 = new byte[attribute_length];
            byteBuffer.get(data);
            attributeReports[i] = new attribute_info(attribute_name_index, attribute_length, data);
        }

        return new code_info(info.attribute_name_index(), info.attribute_length(), info.info(), maxStack, maxLocals, codeLength, code, exceptionTableLength, exceptionTable, attributesCount, attributeReports);
    }
}
