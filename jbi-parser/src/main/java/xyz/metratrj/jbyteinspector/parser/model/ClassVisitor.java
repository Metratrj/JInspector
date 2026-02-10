package xyz.metratrj.jbyteinspector.parser.model;

import xyz.metratrj.jbyteinspector.parser.classfile.*;

import java.util.List;

public interface ClassVisitor {
    // Wird für die Header-Daten aufgerufen
    void visitClassHeader(String className, String superClassName, List<AccessFlags> flags, int major, int minor);

    void visitClassAttributes(attribute_info[] attributes);

    void visitInterface(String interfaceName);

    void visitConstantPool(cp_info[] pool);

    void visitField(String name, String descriptor, List<AccessFlags> flags, attribute_info[] attributes);

    void visitMethod(String name, String descriptor, List<AccessFlags> flags, attribute_info[] attributes);

    void visitEnd();
}
