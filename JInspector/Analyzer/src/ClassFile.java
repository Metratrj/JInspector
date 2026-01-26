import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ClassFile {
    public static final int JAVA_MAGIC = 0xCAFEBABE;

    // see Target
    public static final int CONSTANT_Utf8 = 1;
    public static final int CONSTANT_Unicode = 2;
    public static final int CONSTANT_Integer = 3;
    public static final int CONSTANT_Float = 4;
    public static final int CONSTANT_Long = 5;
    public static final int CONSTANT_Double = 6;
    public static final int CONSTANT_Class = 7;
    public static final int CONSTANT_String = 8;
    public static final int CONSTANT_Fieldref = 9;
    public static final int CONSTANT_Methodref = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameandType = 12;
    public static final int CONSTANT_MethodHandle = 15;
    public static final int CONSTANT_MethodType = 16;
    public static final int CONSTANT_Dynamic = 17;
    public static final int CONSTANT_InvokeDynamic = 18;
    public static final int CONSTANT_Module = 19;
    public static final int CONSTANT_Package = 20;

    public static final int REF_getField = 1;
    public static final int REF_getStatic = 2;
    public static final int REF_putField = 3;
    public static final int REF_putStatic = 4;
    public static final int REF_invokeVirtual = 5;
    public static final int REF_invokeStatic = 6;
    public static final int REF_invokeSpecial = 7;
    public static final int REF_newInvokeSpecial = 8;
    public static final int REF_invokeInterface = 9;

    public static final int MAX_PARAMETERS = 0xff;
    public static final int MAX_DIMENSIONS = 0xff;
    public static final int MAX_CODE = 0xffff;
    public static final int MAX_LOCALS = 0xffff;
    public static final int MAX_STACK = 0xffff;

    public static final int PREVIEW_MINOR_VERSION = 0xffff;
    public static final int MAX_ANNOTATIONS = 0xffff;

    public static void main(String[] args) throws IOException {
        String filePath = "out/production/JInspector/Programm.class";
        DataInputStream in = new DataInputStream(new FileInputStream(filePath));
        try {
            // 1. Magic Number (4 Byte)
            int magic = in.readInt();
            if (magic == 0xCAFEBABE) {
                System.out.printf("MagicNumber: %X Correct\n", magic);
            } else {
                System.out.println("No valid Java Class");
                return;
            }

            // 2. Version
            int minor = in.readUnsignedShort();
            int major = in.readUnsignedShort();
            System.out.printf("Java Version: %d.%d\n", major, minor);

            // 3. Constant Pool
            int cpCount = in.readUnsignedShort();
            System.out.printf("Constant Pool Count: %d\n", cpCount);
            cp_info[] cpPool = new cp_info[cpCount - 1];
            for (int i = 0; i < cpCount; i++) {
                int tag = in.readUnsignedByte();
                System.out.println("Tag: " + tag);
                switch (tag) {
                    case CONSTANT_Class: {
                        System.out.println("Class");
                        int name_index = in.readUnsignedShort();
                        CONSTANT_Class_info entry = new CONSTANT_Class_info(tag, name_index);
                        cpPool[i] = entry;
                        break;
                    }
                    case CONSTANT_Fieldref: {
                        System.out.println("Fieldref");
                        int class_index = in.readUnsignedShort();
                        int name_and_type_index = in.readUnsignedShort();

                        CONSTANT_Fieldref_info entry = new CONSTANT_Fieldref_info(tag, class_index, name_and_type_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_Methodref: {
                        System.out.println("Methodref");
                        int class_index = in.readUnsignedShort();
                        int name_and_type_index = in.readUnsignedShort();
                        CONSTANT_Methodref_info entry = new CONSTANT_Methodref_info(tag, class_index, name_and_type_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_InterfaceMethodref: {
                        System.out.println("InterfaceMethodref");
                        int class_index = in.readUnsignedShort();
                        int name_and_type_index = in.readUnsignedShort();
                        CONSTANT_InterfaceMethodref_info entry = new CONSTANT_InterfaceMethodref_info(tag, class_index, name_and_type_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_String: {
                        System.out.println("String");
                        int string_index = in.readUnsignedShort();
                        CONSTANT_String_info entry = new CONSTANT_String_info(tag, string_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_Integer: {
                        System.out.println("Integer");
                        int bytes = in.readInt();
                        CONSTANT_Integer_info entry = new CONSTANT_Integer_info(tag, bytes);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_Float: {
                        System.out.println("Float");
                        float bytes = in.readFloat();

                    }

                    case CONSTANT_Long: { // 8 Byte
                        i++;
                        break;
                    }
                    case CONSTANT_Double: { // 8 Byte

                        i++;
                        break;
                    }
                }

            }

        } finally {
            in.close();
        }
    }
}
