package xyz.metratrj;

import xyz.metratrj.system.AccessFlags;
import xyz.metratrj.system.attribute_info;


import java.util.List;

public interface ClassVisitor {
    // Wird für die Header-Daten aufgerufen
    void visitClassHeader(String className, String superClassName, List<AccessFlags> flags, int major, int minor);

    void visitConstantPool(ConstantPool pool);

    void visitField(String name, String descriptor, List<AccessFlags> flags, attribute_info[] attributes);

    void visitMethod(String name, String descriptor, List<AccessFlags> flags, attribute_info[] attributes);

    void visitEnd();
}
