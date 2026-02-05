package xyz.metratrj;

import xyz.metratrj.system.*;

import java.util.ArrayList;
import java.util.List;

public class ClassFileAnalyzer {
    private final ClassFile classFile;

    public ClassFileAnalyzer(ClassFile classFile) {
        this.classFile = classFile;
    }

    public void accept(ClassVisitor visitor) {
        // 1. Visit Class Header
        String className = getClassName(classFile.getThisClass());
        String superName = classFile.getSuperClass() == 0 ? null : getClassName(classFile.getSuperClass());
        List<AccessFlags> classFlags = extractClassFlags(classFile.getAccessFlags());

        visitor.visitClassHeader(className, superName, classFlags, classFile.getMajorVersion(), classFile.getMinorVersion());

        // 1.1 Visit Class Attributes
        visitor.visitClassAttributes(classFile.getAttributes());

        // 1.2 Visit Interfaces
        if (classFile.getInterfaces() != null) {
            for (CONSTANT_Class_info interfaceInfo : classFile.getInterfaces()) {
                String interfaceName = getUtf8Value(interfaceInfo.name_index);
                visitor.visitInterface(interfaceName);
            }
        }

        // 2. Visit Constant Pool
        ConstantPool pool = new ConstantPool(classFile.getConstantPool());
        visitor.visitConstantPool(pool);

        // 3. Visit Fields
        if (classFile.getFields() != null) {
            for (field_info field : classFile.getFields()) {
                String name = getUtf8Value(field.getName_index());
                String descriptor = getUtf8Value(field.getDescriptor_index());
                List<AccessFlags> fieldFlags = extractFieldFlags(field.getAccess_flags());
                
                visitor.visitField(name, descriptor, fieldFlags, field.getAttributes());
            }
        }

        // 4. Visit Methods
        if (classFile.getMethods() != null) {
            for (method_info method : classFile.getMethods()) {
                String name = getUtf8Value(method.getNameIndex());
                String descriptor = getUtf8Value(method.getDescriptionIndex());
                List<AccessFlags> methodFlags = extractMethodFlags(method.getAccessFlags());

                visitor.visitMethod(name, descriptor, methodFlags, method.getAttributes());
            }
        }

        // 5. End
        visitor.visitEnd();
    }

    private String getClassName(int index) {
        CONSTANT_Class_info classInfo = classFile.getConstantPoolItem(index, CONSTANT_Class_info.class);
        if (classInfo == null) return null;
        return getUtf8Value(classInfo.name_index);
    }

    private String getUtf8Value(int index) {
        CONSTANT_Utf8_info utf8Info = classFile.getConstantPoolItem(index, CONSTANT_Utf8_info.class);
        return utf8Info != null ? utf8Info.getValue() : null;
    }

    private List<AccessFlags> extractCommonFlags(int accessFlags) {
        List<AccessFlags> flags = new ArrayList<>();
        if ((accessFlags & ClassFile.ACC_PUBLIC) != 0) flags.add(AccessFlags.PUBLIC);
        if ((accessFlags & ClassFile.ACC_PRIVATE) != 0) flags.add(AccessFlags.PRIVATE);
        if ((accessFlags & ClassFile.ACC_PROTECTED) != 0) flags.add(AccessFlags.PROTECTED);
        if ((accessFlags & ClassFile.ACC_STATIC) != 0) flags.add(AccessFlags.STATIC);
        if ((accessFlags & ClassFile.ACC_FINAL) != 0) flags.add(AccessFlags.FINAL);
        if ((accessFlags & ClassFile.ACC_SYNTHETIC) != 0) flags.add(AccessFlags.SYNTHETIC);
        return flags;
    }

    private List<AccessFlags> extractClassFlags(int accessFlags) {
        List<AccessFlags> flags = extractCommonFlags(accessFlags);
        if ((accessFlags & ClassFile.ACC_INTERFACE) != 0) flags.add(AccessFlags.INTERFACE);
        if ((accessFlags & ClassFile.ACC_ABSTRACT) != 0) flags.add(AccessFlags.ABSTRACT);
        if ((accessFlags & ClassFile.ACC_ANNOTATION) != 0) flags.add(AccessFlags.ANNOTATION);
        if ((accessFlags & ClassFile.ACC_ENUM) != 0) flags.add(AccessFlags.ENUM);
        if ((accessFlags & ClassFile.ACC_MODULE) != 0) flags.add(AccessFlags.MODULE);
        if ((accessFlags & ClassFile.ACC_SUPER) != 0) flags.add(AccessFlags.SUPER); // ClassFile.ACC_SUPER is same as ACC_SYNCHRONIZED but context matters
        return flags;
    }

    private List<AccessFlags> extractFieldFlags(int accessFlags) {
        List<AccessFlags> flags = extractCommonFlags(accessFlags);
        if ((accessFlags & ClassFile.ACC_VOLATILE) != 0) flags.add(AccessFlags.VOLATILE);
        if ((accessFlags & ClassFile.ACC_TRANSIENT) != 0) flags.add(AccessFlags.TRANSIENT);
        if ((accessFlags & ClassFile.ACC_ENUM) != 0) flags.add(AccessFlags.ENUM);
        return flags;
    }

    private List<AccessFlags> extractMethodFlags(int accessFlags) {
        List<AccessFlags> flags = extractCommonFlags(accessFlags);
        if ((accessFlags & ClassFile.ACC_SYNCHRONIZED) != 0) flags.add(AccessFlags.SYNCHRONIZED);
        if ((accessFlags & ClassFile.ACC_BRIDGE) != 0) flags.add(AccessFlags.BRIDGE);
        if ((accessFlags & ClassFile.ACC_VARARGS) != 0) flags.add(AccessFlags.VARARGS);
        if ((accessFlags & ClassFile.ACC_NATIVE) != 0) flags.add(AccessFlags.NATIVE);
        if ((accessFlags & ClassFile.ACC_ABSTRACT) != 0) flags.add(AccessFlags.ABSTRACT);
        if ((accessFlags & ClassFile.ACC_STRICT) != 0) flags.add(AccessFlags.STRICT);
        return flags;
    }
}
