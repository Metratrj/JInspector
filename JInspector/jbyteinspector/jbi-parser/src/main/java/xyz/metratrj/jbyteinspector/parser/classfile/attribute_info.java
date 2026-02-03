package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Arrays;

public class attribute_info {
    int    attribute_name_index;
    int    attribute_length;
    byte[] info;

    public attribute_info(int attribute_name_index, int attribute_length, byte[] info) {
        this.attribute_name_index = attribute_name_index;
        this.attribute_length     = attribute_length;
        this.info                 = info;
    }

    public int getAttribute_name_index() {
        return attribute_name_index;
    }

    public int getAttribute_length() {
        return attribute_length;
    }

    public byte[] getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "attribute_info{" +
                "attribute_name_index=" + attribute_name_index +
                ", attribute_length=" + attribute_length +
                ", info=" + Arrays.toString(info) +
                '}';
    }
}
