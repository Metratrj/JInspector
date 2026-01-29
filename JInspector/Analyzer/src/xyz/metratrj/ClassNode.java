package xyz.metratrj;

public class ClassNode {
    public int             version;
    public int             access;
    public String          name;
    public String          superName;
    public String[]        interfaces;
    public FieldNode[]     fields;
    public MethodNode[]    methods;
    public AttributeNode[] attributes;
}
