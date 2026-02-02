package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Arrays;

public class field_info {
    int              access_flags;
    int              name_index;
    int              descriptor_index;
    int              attributes_count;
    attribute_info[] attributes;

    public field_info(int access_flags, int name_index, int descriptor_index, int attributes_count,
                      attribute_info[] attributes) {
        this.access_flags     = access_flags;
        this.name_index       = name_index;
        this.descriptor_index = descriptor_index;
        this.attributes_count = attributes_count;
        this.attributes       = attributes;
    }

    @Override
    public String toString() {
        return "field_info{" +
                "access_flags=" + access_flags +
                ", name_index=" + name_index +
                ", descriptor_index=" + descriptor_index +
                ", attributes_count=" + attributes_count +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }

    public int getAccess_flags() {
        return access_flags;
    }

    public int getName_index() {
        return name_index;
    }

    public int getDescriptor_index() {
        return descriptor_index;
    }

    public int getAttributes_count() {
        return attributes_count;
    }

    public attribute_info[] getAttributes() {
        return attributes;
    }
}
