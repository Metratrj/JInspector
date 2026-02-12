package xyz.metratrj.jbyteinspector.parser.utils;

import xyz.metratrj.jbyteinspector.parser.classfile.AccessFlags;
import xyz.metratrj.jbyteinspector.parser.classfile.ClassFile;

import java.util.EnumSet;
import java.util.Set;

public class AccessFlagUtils {

    public static Set<AccessFlags> extract(int accessFlags) {
        Set<AccessFlags> flags = EnumSet.noneOf(AccessFlags.class);
        
        if ((accessFlags & ClassFile.ACC_PUBLIC) != 0) flags.add(AccessFlags.PUBLIC);
        if ((accessFlags & ClassFile.ACC_PRIVATE) != 0) flags.add(AccessFlags.PRIVATE);
        if ((accessFlags & ClassFile.ACC_PROTECTED) != 0) flags.add(AccessFlags.PROTECTED);
        if ((accessFlags & ClassFile.ACC_STATIC) != 0) flags.add(AccessFlags.STATIC);
        if ((accessFlags & ClassFile.ACC_FINAL) != 0) flags.add(AccessFlags.FINAL);
        if ((accessFlags & ClassFile.ACC_SYNCHRONIZED) != 0) flags.add(AccessFlags.SYNCHRONIZED);
        if ((accessFlags & ClassFile.ACC_VOLATILE) != 0) flags.add(AccessFlags.VOLATILE); // Same as BRIDGE
        if ((accessFlags & ClassFile.ACC_TRANSIENT) != 0) flags.add(AccessFlags.TRANSIENT); // Same as VARARGS
        if ((accessFlags & ClassFile.ACC_NATIVE) != 0) flags.add(AccessFlags.NATIVE);
        if ((accessFlags & ClassFile.ACC_INTERFACE) != 0) flags.add(AccessFlags.INTERFACE);
        if ((accessFlags & ClassFile.ACC_ABSTRACT) != 0) flags.add(AccessFlags.ABSTRACT);
        if ((accessFlags & ClassFile.ACC_STRICT) != 0) flags.add(AccessFlags.STRICT);
        if ((accessFlags & ClassFile.ACC_SYNTHETIC) != 0) flags.add(AccessFlags.SYNTHETIC);
        if ((accessFlags & ClassFile.ACC_ANNOTATION) != 0) flags.add(AccessFlags.ANNOTATION);
        if ((accessFlags & ClassFile.ACC_ENUM) != 0) flags.add(AccessFlags.ENUM);
        if ((accessFlags & ClassFile.ACC_MODULE) != 0) flags.add(AccessFlags.MODULE);
        
        // Note: Some flags overlap (VOLATILE/BRIDGE, TRANSIENT/VARARGS). 
        // Context (Class vs Method vs Field) matters for strict correctness.
        // For now, this generic extraction maps to the primary names.
        
        return flags;
    }
    
    public static Set<AccessFlags> extractForMethod(int accessFlags) {
        Set<AccessFlags> flags = extract(accessFlags);
        // Fix up overlaps for methods
        if ((accessFlags & ClassFile.ACC_BRIDGE) != 0) flags.add(AccessFlags.BRIDGE); 
        if ((accessFlags & ClassFile.ACC_VARARGS) != 0) flags.add(AccessFlags.VARARGS);
        flags.remove(AccessFlags.VOLATILE); // Volatile is field only
        flags.remove(AccessFlags.TRANSIENT); // Transient is field only
        return flags;
    }
    
    public static Set<AccessFlags> extractForField(int accessFlags) {
        Set<AccessFlags> flags = extract(accessFlags);
        // Fix up overlaps for fields
        flags.remove(AccessFlags.BRIDGE);
        flags.remove(AccessFlags.VARARGS);
        return flags;
    }
}
