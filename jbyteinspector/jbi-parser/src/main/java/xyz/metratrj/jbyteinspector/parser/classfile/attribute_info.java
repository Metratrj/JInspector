package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Arrays;
import java.util.Objects;

public class attribute_info {
    private final int    attribute_name_index;
    private final int    attribute_length;
    private final byte[] info;

    public attribute_info(int attribute_name_index, int attribute_length, byte[] info) {
        this.attribute_name_index = attribute_name_index;
        this.attribute_length     = attribute_length;
        this.info                 = info;
    }

    @Override
    public String toString() {
        return "attribute_info{" +
                "attribute_name_index=" + attribute_name_index +
                ", attribute_length=" + attribute_length +
                ", info=" + Arrays.toString(info) +
                '}';
    }

    public int attribute_name_index() {
        return attribute_name_index;
    }

    public int attribute_length() {
        return attribute_length;
    }

    public byte[] info() {
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (attribute_info) obj;
        return this.attribute_name_index == that.attribute_name_index &&
                this.attribute_length == that.attribute_length &&
                Objects.equals(this.info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute_name_index, attribute_length, info);
    }

}