package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Arrays;

public class method_info {
    int              accessFlags;
    int              nameIndex;
    int              descriptionIndex;
    int              attributesCount;
    attribute_info[] attributes;

    public method_info(int accessFlags, int nameIndex, int descriptionIndex, int attributesCount,
                       attribute_info[] attributes) {
        this.accessFlags      = accessFlags;
        this.nameIndex        = nameIndex;
        this.descriptionIndex = descriptionIndex;
        this.attributesCount  = attributesCount;
        this.attributes       = attributes;
    }

    @Override
    public String toString() {
        return "method_info{" +
                "access_flags=" + accessFlags +
                ", name_index=" + nameIndex +
                ", description_index=" + descriptionIndex +
                ", attributes_count=" + attributesCount +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getDescriptionIndex() {
        return descriptionIndex;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public attribute_info[] getAttributes() {
        return attributes;
    }
}
