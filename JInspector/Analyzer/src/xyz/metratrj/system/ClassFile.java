package xyz.metratrj.system;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ClassFile {
    public static final int JAVA_MAGIC = 0xCAFEBABE;

    // see Target
    public static final int CONSTANT_Utf8               = 1;
    public static final int CONSTANT_Unicode            = 2;
    public static final int CONSTANT_Integer            = 3;
    public static final int CONSTANT_Float              = 4;
    public static final int CONSTANT_Long               = 5;
    public static final int CONSTANT_Double             = 6;
    public static final int CONSTANT_Class              = 7;
    public static final int CONSTANT_String             = 8;
    public static final int CONSTANT_Fieldref           = 9;
    public static final int CONSTANT_Methodref          = 10;
    public static final int CONSTANT_InterfaceMethodref = 11;
    public static final int CONSTANT_NameandType        = 12;
    public static final int CONSTANT_MethodHandle       = 15;
    public static final int CONSTANT_MethodType         = 16;
    public static final int CONSTANT_Dynamic            = 17;
    public static final int CONSTANT_InvokeDynamic      = 18;
    public static final int CONSTANT_Module             = 19;
    public static final int CONSTANT_Package            = 20;

    public static final int REF_getField         = 1;
    public static final int REF_getStatic        = 2;
    public static final int REF_putField         = 3;
    public static final int REF_putStatic        = 4;
    public static final int REF_invokeVirtual    = 5;
    public static final int REF_invokeStatic     = 6;
    public static final int REF_invokeSpecial    = 7;
    public static final int REF_newInvokeSpecial = 8;
    public static final int REF_invokeInterface  = 9;

    public static final int MAX_PARAMETERS = 0xff;
    public static final int MAX_DIMENSIONS = 0xff;
    public static final int MAX_CODE       = 0xffff;
    public static final int MAX_LOCALS     = 0xffff;
    public static final int MAX_STACK      = 0xffff;

    public static final int PREVIEW_MINOR_VERSION = 0xffff;
    public static final int MAX_ANNOTATIONS       = 0xffff;

    public static final int ACC_PUBLIC     = 0x0001;
    public static final int ACC_FINAL      = 0x0001;
    public static final int ACC_SUPER      = 0x0001;
    public static final int ACC_INTERFACE  = 0x0001;
    public static final int ACC_ABSTRACT   = 0x0001;
    public static final int ACC_SYNTHETIC  = 0x0001;
    public static final int ACC_ANNOTATION = 0x0001;
    public static final int ACC_ENUM       = 0x0001;
    public static final int ACC_MODULE     = 0x0001;


    private int                   magic;
    private int                   minorVersion;
    private int                   majorVersion;
    private int                   constantPoolCount;
    private cp_info[]             constantPool;
    private int                   accessFlags;
    private int                   thisClass;
    private int                   superClass;
    private int                   interfacesCount;
    private CONSTANT_Class_info[] interfaces;
    private int                   fieldsCount;
    private field_info[]          fields;
    private int                   methodsCount;
    private method_info[]         methods;
    private int                   attributesCount;
    private attribute_info[]      attributes;

    private ClassFile() {
    }



    public static ClassFile parse(Path path) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(path.toString()))) {
            ClassFile classFile = new ClassFile();
            classFile.parseMagic(in);
            classFile.parseVersion(in);
            classFile.parseConstantPool(in);
            classFile.parseClassInfo(in);
            classFile.parseInterfaces(in);
            classFile.parseFields(in);
            classFile.parseMethods(in);
            classFile.parseAttributes(in);
            return classFile;
        }
    }

    private void parseMagic(DataInputStream in) throws IOException {
        magic = in.readInt();
        if (magic != JAVA_MAGIC) {
            throw new IOException("Invalid magic number");
        }
    }

    private void parseVersion(DataInputStream in) throws IOException {
        minorVersion = in.readUnsignedShort();
        majorVersion = in.readUnsignedShort();
    }

    private void parseConstantPool(DataInputStream in) throws IOException {
        constantPoolCount = in.readUnsignedShort();
        constantPool      = new cp_info[constantPoolCount];
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case CONSTANT_Class:
                    constantPool[i] = new CONSTANT_Class_info(tag, in.readUnsignedShort());
                    break;
                case CONSTANT_Fieldref:
                    constantPool[i] = new CONSTANT_Fieldref_info(tag, in.readUnsignedShort(), in.readUnsignedShort());
                    break;
                case CONSTANT_Methodref:
                    constantPool[i] = new CONSTANT_Methodref_info(tag, in.readUnsignedShort(), in.readUnsignedShort());
                    break;
                case CONSTANT_InterfaceMethodref:
                    constantPool[i] = new CONSTANT_InterfaceMethodref_info(tag, in.readUnsignedShort(), in.readUnsignedShort());
                    break;
                case CONSTANT_String:
                    constantPool[i] = new CONSTANT_String_info(tag, in.readUnsignedShort());
                    break;
                case CONSTANT_Integer:
                    constantPool[i] = new CONSTANT_Integer_info(tag, in.readInt());
                    break;
                case CONSTANT_Float:
                    constantPool[i] = new CONSTANT_Float_info(tag, in.readFloat());
                    break;
                case CONSTANT_Long:
                    constantPool[i] = new CONSTANT_Long_info(tag, in.readLong());
                    i++;
                    break;
                case CONSTANT_Double:
                    constantPool[i] = new CONSTANT_Double_info(tag, in.readDouble());
                    i++;
                    break;
                case CONSTANT_NameandType:
                    constantPool[i] = new CONSTANT_NameAndType_info(tag, in.readUnsignedShort(), in.readUnsignedShort());
                    break;
                case CONSTANT_Utf8:
                    constantPool[i] = new CONSTANT_Utf8_info(tag, in.readUTF());
                    break;
                case CONSTANT_MethodHandle:
                    constantPool[i] = new CONSTANT_MethodHandle_info(tag, in.readUnsignedByte(), in.readUnsignedShort());
                    break;
                case CONSTANT_MethodType:
                    constantPool[i] = new CONSTANT_MethodType_info(tag, in.readUnsignedShort());
                    break;
                case CONSTANT_InvokeDynamic:
                    constantPool[i] = new CONSTANT_InvokeDynamic_info(tag, in.readUnsignedShort(), in.readUnsignedShort());
                    break;
                default:
                    // ignore unknown tags
                    break;
            }
        }
    }

    private void parseClassInfo(DataInputStream in) throws IOException {
        accessFlags = in.readUnsignedShort();
        thisClass   = in.readUnsignedShort();
        superClass  = in.readUnsignedShort();
    }

    private void parseInterfaces(DataInputStream in) throws IOException {
        interfacesCount = in.readUnsignedShort();
        interfaces      = new CONSTANT_Class_info[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            int interfaceIndex = in.readUnsignedShort();
            interfaces[i] = (CONSTANT_Class_info) constantPool[interfaceIndex];
        }
    }

    private void parseFields(DataInputStream in) throws IOException {
        fieldsCount = in.readUnsignedShort();
        fields      = new field_info[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = parseFieldInfo(in);
        }
    }

    private field_info parseFieldInfo(DataInputStream in) throws IOException {
        int              accessFlags     = in.readUnsignedShort();
        int              nameIndex       = in.readUnsignedShort();
        int              descriptorIndex = in.readUnsignedShort();
        int              attributesCount = in.readUnsignedShort();
        attribute_info[] attributes      = parseAttributesArray(in, attributesCount);
        return new field_info(accessFlags, nameIndex, descriptorIndex, attributesCount, attributes);
    }

    private void parseMethods(DataInputStream in) throws IOException {
        methodsCount = in.readUnsignedShort();
        methods      = new method_info[methodsCount];
        for (int i = 0; i < methodsCount; i++) {
            methods[i] = parseMethodInfo(in);
        }
    }

    private method_info parseMethodInfo(DataInputStream in) throws IOException {
        int              accessFlags     = in.readUnsignedShort();
        int              nameIndex       = in.readUnsignedShort();
        int              descriptorIndex = in.readUnsignedShort();
        int              attributesCount = in.readUnsignedShort();
        attribute_info[] attributes      = parseAttributesArray(in, attributesCount);
        return new method_info(accessFlags, nameIndex, descriptorIndex, attributesCount, attributes);
    }

    private void parseAttributes(DataInputStream in) throws IOException {
        attributesCount = in.readUnsignedShort();
        attributes      = parseAttributesArray(in, attributesCount);
    }

    private attribute_info[] parseAttributesArray(DataInputStream in, int count) throws IOException {
        attribute_info[] attributes = new attribute_info[count];
        for (int i = 0; i < count; i++) {
            attributes[i] = parseAttributeInfo(in);
        }
        return attributes;
    }

    private attribute_info parseAttributeInfo(DataInputStream in) throws IOException {
        int    attributeNameIndex = in.readUnsignedShort();
        int    attributeLength    = in.readInt();
        byte[] info               = new byte[attributeLength];
        in.readFully(info);
        return new attribute_info(attributeNameIndex, attributeLength, info);
    }
}
