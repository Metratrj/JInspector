package xyz.metratrj.jbyteinspector.parser;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Minimal constant pool parser.
 */
public class ConstantPool {
    private final Object[] entries;

    public ConstantPool(DataInputStream din) throws IOException {
        int count = din.readUnsignedShort();
        entries = new Object[count];
        for (int i = 1; i < count; i++) {
            int tag = din.readUnsignedByte();
            switch (tag) {
                case 1: // UTF8
                    entries[i] = din.readUTF();
                    break;
                case 3: // Integer
                    entries[i] = din.readInt();
                    break;
                case 4: // Float
                    entries[i] = din.readFloat();
                    break;
                case 5: // Long
                    entries[i] = din.readLong();
                    i++;
                    break;
                case 6: // Double
                    entries[i] = din.readDouble();
                    i++;
                    break;
                case 7: // Class
                case 8: // String
                case 16: // MethodType
                case 19: // Module
                case 20: // Package
                    entries[i] = din.readUnsignedShort();
                    break;
                case 9: // Fieldref
                case 10: // Methodref
                case 11: // InterfaceMethodref
                case 12: // NameAndType
                case 18: // InvokeDynamic
                    din.readInt(); // skip
                    break;
                case 15: // MethodHandle
                    din.readByte();
                    din.readUnsignedShort();
                    break;
                case 17: // Dynamic
                    din.readInt();
                    break;
                default:
                    throw new IOException("Unknown constant pool tag: " + tag);
            }
        }
    }

    public String getUtf8(int index) {
        return (String) entries[index];
    }

    public String getClassName(int index) {
        int utf8Index = (Integer) entries[index];
        return getUtf8(utf8Index);
    }

    public Object getValue(int index) {
        Object val = entries[index];
        if (val instanceof Integer && (Integer) val < entries.length && entries[(Integer) val] instanceof String) {
            // This is a rough simplification for String/Class constants
            return entries[(Integer) val];
        }
        return val;
    }
}
