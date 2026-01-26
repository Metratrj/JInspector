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

        // Open the class file for reading.
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
            cp_info[] cpPool = new cp_info[cpCount];

            // Iterate through the constant pool and parse each entry based on its tag.
            for (int i = 1; i < cpCount; i++) {
                int tag = in.readUnsignedByte();
                System.out.println("\n\nTag: " + tag);
                switch (tag) {
                    case CONSTANT_Class: {
                        System.out.println("Class");
                        int name_index = in.readUnsignedShort();
                        CONSTANT_Class_info entry = new CONSTANT_Class_info(tag, name_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
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
                        CONSTANT_Float_info entry = new CONSTANT_Float_info(tag, bytes);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_Long: { // 8 Byte
                        System.out.println("Long");
                        long bytes = in.readLong();
                        CONSTANT_Long_info entry = new CONSTANT_Long_info(tag, bytes);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        i++;
                        break;
                    }
                    case CONSTANT_Double: { // 8 Byte
                        System.out.println("Double");
                        double bytes = in.readDouble();
                        CONSTANT_Double_info entry = new CONSTANT_Double_info(tag, bytes);
                        cpPool[i] = entry;
                        System.out.println(entry);

                        i++;
                        break;
                    }
                    case CONSTANT_NameandType: {
                        System.out.println("NameAndType");
                        int name_index = in.readUnsignedShort();
                        int description_index = in.readUnsignedShort();
                        CONSTANT_NameAndType_info entry = new CONSTANT_NameAndType_info(tag, name_index, description_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_Utf8: {
                        System.out.println("UTF8");

                        // No need to manually parse the modified UTF8 logic
                        // readUTF brings that logic already internally
                        String data = in.readUTF();

                        CONSTANT_Utf8_info entry = new CONSTANT_Utf8_info(tag, data);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_MethodHandle: {
                        System.out.println("MethodHandle");
                        int reference_type = in.readUnsignedByte();
                        int reference_index = in.readUnsignedShort();
                        CONSTANT_MethodHandle_info entry = new CONSTANT_MethodHandle_info(tag, reference_type, reference_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_MethodType: {
                        System.out.println("MethodType");
                        int description_index = in.readUnsignedShort();
                        CONSTANT_MethodType_info entry = new CONSTANT_MethodType_info(tag, description_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                    case CONSTANT_InvokeDynamic: {
                        System.out.println("InvokeDynamic");
                        int bootstrap_method_attr_index = in.readUnsignedShort();
                        int name_and_type_index = in.readUnsignedShort();

                        CONSTANT_InvokeDynamic_info entry = new CONSTANT_InvokeDynamic_info(tag, bootstrap_method_attr_index, name_and_type_index);
                        cpPool[i] = entry;
                        System.out.println(entry);
                        break;
                    }
                }

            }

            // 4. Access Flags
            int access_flags = in.readUnsignedShort();
            System.out.printf("Access Flags: %4X\n", access_flags);

            // 5. this_class
            int this_class = in.readUnsignedShort();
            int super_class = in.readUnsignedShort();

            System.out.printf("this_class: %d\nsuper_class %d\n", this_class, super_class);

            // 6. interfaces
            int interfaces_count = in.readUnsignedShort();
            System.out.println(interfaces_count);
            CONSTANT_Class_info[] interfaces = new CONSTANT_Class_info[interfaces_count];
            for (int i = 0; i < interfaces_count; i++) {
                System.out.println("Interface");
                int name_index = in.readUnsignedShort();
                CONSTANT_Class_info entry = new CONSTANT_Class_info(0, name_index);
                interfaces[i] = entry;
                System.out.println(entry);
            }

            // 7. Fields
            int fields_count = in.readUnsignedShort();
            System.out.println(fields_count);
            field_info[] fields = new field_info[fields_count];
            for (int i = 0; i < fields_count; i++) {
                System.out.println("Field");
                int field_access_flags = in.readUnsignedShort();
                int name_index = in.readUnsignedShort();
                int descriptor_index = in.readUnsignedShort();
                int attributes_count = in.readUnsignedShort();
                attribute_info[] attributes = new attribute_info[attributes_count];
                for (int j = 0; j < attributes_count; j++) {
                    int attribute_name_index = in.readUnsignedShort();
                    int attribute_length = in.readUnsignedShort();
                    byte[] info = new byte[attribute_length];
                    in.read(info);
                    attribute_info attributeInfo = new attribute_info(attribute_name_index, attribute_length, info);
                    attributes[j] = attributeInfo;
                    System.out.println(attributeInfo);
                }
                field_info fieldInfo = new field_info(field_access_flags, name_index, descriptor_index, attributes_count, attributes);
                fields[i] = fieldInfo;
                System.out.println(fieldInfo);
            }

            // 8. Methods
            int methods_count = in.readUnsignedShort();
            System.out.println(methods_count);
            method_info[] methods = new method_info[methods_count];
            for (int i = 0; i < methods_count; i++) {
                System.out.println("Method");
                int method_access_flags = in.readUnsignedShort();
                int name_index = in.readUnsignedShort();
                System.out.println(cpPool[name_index]);
                int description_index = in.readUnsignedShort();
                int attributes_count = in.readUnsignedShort();
                attribute_info[] attributes = new attribute_info[attributes_count];
                for (int j = 0; j < attributes_count; j++) {
                    int attribute_name_index = in.readUnsignedShort();
                    int attribute_length = in.readUnsignedShort();
                    byte[] info = new byte[attribute_length];
                    in.read(info);
                    attribute_info attributeInfo = new attribute_info(attribute_name_index, attribute_length, info);
                    attributes[j] = attributeInfo;
                    System.out.println(attributeInfo);
                }
                method_info methodInfo = new method_info(method_access_flags, name_index, description_index, attributes_count, attributes);
                methods[i] = methodInfo;
                System.out.println(methodInfo);
            }

            int attributes_count = in.readUnsignedShort();
            attribute_info[] attributes = new attribute_info[attributes_count];
            for (int i = 0; i < attributes_count; i++) {
                int attribute_name_index = in.readUnsignedShort();
                int attribute_length = in.readUnsignedShort();
                byte[] info = new byte[attribute_length];
                in.read(info);
                attribute_info attributeInfo = new attribute_info(attribute_name_index, attribute_length, info);
                attributes[i] = attributeInfo;
                System.out.println(attributeInfo);
            }



        } finally {
            in.close();
        }
    }
}
