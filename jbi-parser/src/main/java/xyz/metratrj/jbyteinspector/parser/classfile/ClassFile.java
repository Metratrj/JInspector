package xyz.metratrj.jbyteinspector.parser.classfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Java class file structure as defined by the Java Virtual Machine Specification (JVMS).
 * This class handles the parsing of binary .class files and provides access to their constituent parts
 * such as the constant pool, fields, methods, and attributes.
 */
public class ClassFile implements ICodes{

    private static final Logger LOGGER = Logger.getLogger(ClassFile.class.getName());

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

    /**
     * Parses a Java class file from the specified path.
     *
     * @param path The path to the .class file.
     * @return A parsed {@link ClassFile} object.
     * @throws IOException If an I/O error occurs or the file is not a valid class file.
     */
    public static ClassFile parse(Path path) throws IOException {
        LOGGER.info("Starting to parse class file: " + path);
        try (DataInputStream in = new DataInputStream(Files.newInputStream(path))) {
            ClassFile classFile = new ClassFile();
            classFile.parseMagic(in);
            classFile.parseVersion(in);
            classFile.parseConstantPool(in);
            classFile.parseClassInfo(in);
            classFile.parseInterfaces(in);
            classFile.parseFields(in);
            classFile.parseMethods(in);
            classFile.parseAttributes(in);
            LOGGER.info("Successfully parsed class file: " + path);
            return classFile;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error parsing class file: " + path, e);
            throw e;
        }
    }

    /**
     * Gets the magic number of the class file (should be 0xCAFEBABE).
     * @return the magic number.
     */
    public int getMagic() {
        return magic;
    }

    /**
     * Gets the minor version of the class file.
     * @return the minor version.
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the major version of the class file.
     * @return the major version.
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Gets the number of entries in the constant pool plus one.
     * @return the constant pool count.
     */
    public int getConstantPoolCount() {
        return constantPoolCount;
    }

    /**
     * Gets the raw constant pool array.
     * @return the array of {@link cp_info}.
     */
    public cp_info[] getConstantPool() {
        return constantPool;
    }

    /**
     * Retrieves a specific item from the constant pool by its index.
     *
     * @param index The 1-based index into the constant pool.
     * @return The {@link cp_info} entry, or null if the index is invalid.
     */
    public cp_info getConstantPoolItem(int index) {
        if (constantPool != null && index > 0 && index < constantPool.length) {
            return constantPool[index];
        }
        return null;
    }

    /**
     * Retrieves a specific item from the constant pool and casts it to the expected type.
     *
     * @param index The 1-based index into the constant pool.
     * @param type  The expected class of the entry.
     * @param <T>   The type of the entry.
     * @return The cast entry, or null if the index is invalid or the type doesn't match.
     */
    public <T extends cp_info> T getConstantPoolItem(int index, Class<T> type) {
        cp_info item = getConstantPoolItem(index);
        if (type.isInstance(item)) {
            return type.cast(item);
        }
        return null;
    }

    /**
     * Gets the tag byte for a constant pool entry.
     *
     * @param index The 1-based index into the constant pool.
     * @return The tag value, or 0 if the index is invalid.
     */
    public int getConstantPoolTag(int index) {
        cp_info item = getConstantPoolItem(index);
        if (item != null) {
            return item.tag;
        }
        return 0;
    }

    /**
     * Gets the access flags for this class.
     * @return the access flags mask.
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
     * Gets the constant pool index of the {@link CONSTANT_Class_info} representing this class.
     * @return the index.
     */
    public int getThisClass() {
        return thisClass;
    }

    /**
     * Gets the constant pool index of the {@link CONSTANT_Class_info} representing the superclass.
     * @return the index, or 0 if this class is {@link Object}.
     */
    public int getSuperClass() {
        return superClass;
    }

    /**
     * Gets the number of interfaces directly implemented by this class.
     * @return the interfaces count.
     */
    public int getInterfacesCount() {
        return interfacesCount;
    }

    /**
     * Gets the array of interface information.
     * @return the array of {@link CONSTANT_Class_info}.
     */
    public CONSTANT_Class_info[] getInterfaces() {
        return interfaces;
    }

    /**
     * Gets the number of fields declared by this class.
     * @return the fields count.
     */
    public int getFieldsCount() {
        return fieldsCount;
    }

    /**
     * Gets the array of field information.
     * @return the array of {@link field_info}.
     */
    public field_info[] getFields() {
        return fields;
    }

    /**
     * Gets the number of methods declared by this class.
     * @return the methods count.
     */
    public int getMethodsCount() {
        return methodsCount;
    }

    /**
     * Gets the array of method information.
     * @return the array of {@link method_info}.
     */
    public method_info[] getMethods() {
        return methods;
    }

    /**
     * Gets the number of attributes of this class.
     * @return the attributes count.
     */
    public int getAttributesCount() {
        return attributesCount;
    }

    /**
     * Gets the array of class attributes.
     * @return the array of {@link attribute_info}.
     */
    public attribute_info[] getAttributes() {
        return attributes;
    }

    private void parseMagic(DataInputStream in) throws IOException {
        magic = in.readInt();
        if (magic != JAVA_MAGIC) {
            LOGGER.severe("Invalid magic number: " + Integer.toHexString(magic));
            throw new IOException("Invalid magic number");
        }
        LOGGER.fine("Magic number verified");
    }

    private void parseVersion(DataInputStream in) throws IOException {
        minorVersion = in.readUnsignedShort();
        majorVersion = in.readUnsignedShort();
        LOGGER.fine("Class version: " + majorVersion + "." + minorVersion);
    }

    private void parseConstantPool(DataInputStream in) throws IOException {
        constantPoolCount = in.readUnsignedShort();
        LOGGER.fine("Constant pool count: " + constantPoolCount);
        constantPool = new cp_info[constantPoolCount];
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
                    LOGGER.warning("Unknown constant pool tag: " + tag);
                    break;
            }
        }
    }

    private void parseClassInfo(DataInputStream in) throws IOException {
        accessFlags = in.readUnsignedShort();
        thisClass   = in.readUnsignedShort();
        superClass  = in.readUnsignedShort();
        LOGGER.fine("Class info parsed. Access flags: " + Integer.toHexString(accessFlags));
    }

    private void parseInterfaces(DataInputStream in) throws IOException {
        interfacesCount = in.readUnsignedShort();
        LOGGER.fine("Interfaces count: " + interfacesCount);
        interfaces = new CONSTANT_Class_info[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            int interfaceIndex = in.readUnsignedShort();
            interfaces[i] = (CONSTANT_Class_info) constantPool[interfaceIndex];
        }
    }

    private void parseFields(DataInputStream in) throws IOException {
        fieldsCount = in.readUnsignedShort();
        LOGGER.fine("Fields count: " + fieldsCount);
        fields = new field_info[fieldsCount];
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
        LOGGER.fine("Methods count: " + methodsCount);
        methods = new method_info[methodsCount];
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
        LOGGER.fine("Attributes count: " + attributesCount);
        attributes = parseAttributesArray(in, attributesCount);
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

    @Override
    public String toString() {
        return "ClassFile{" +
                "magic=" + Integer.toHexString(magic) +
                ", version=" + majorVersion + "." + minorVersion +
                ", constantPoolCount=" + constantPoolCount +
                ", accessFlags=" + Integer.toHexString(accessFlags) +
                ", thisClass=" + thisClass +
                ", superClass=" + superClass +
                ", interfacesCount=" + interfacesCount +
                ", fieldsCount=" + fieldsCount +
                ", methodsCount=" + methodsCount +
                ", attributesCount=" + attributesCount +
                '}';
    }
}
