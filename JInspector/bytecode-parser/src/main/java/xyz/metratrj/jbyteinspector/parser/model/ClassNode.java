package xyz.metratrj.jbyteinspector.parser.model;

public class ClassNode {
    public ClassNode       parent;
    public int             version;
    public int             access;
    public String          name;
    public String          superName;
    public String[]        interfaces;
    public FieldNode[]     fields;
    public MethodNode[]    methods;
    public AttributeNode[] attributes;
}
