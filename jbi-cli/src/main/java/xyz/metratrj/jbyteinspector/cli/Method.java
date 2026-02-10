package xyz.metratrj.jbyteinspector.cli;


/*
* method_info {
    u2             access_flags;
    u2             name_index;
    u2             descriptor_index;
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
* */
public class Method extends Entity {
    int AccessFlags;
    int NameIndex;
    int DescriptorIndex;
    int AttributesCount;
    Attribute[] Attributes;
}
