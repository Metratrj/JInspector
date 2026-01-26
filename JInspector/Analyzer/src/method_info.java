import java.util.Arrays;

public class method_info {
    int access_flags;
    int name_index;
    int description_index;
    int attributes_count;
    attribute_info[] attributes;

    public method_info(int access_flags, int name_index, int description_index, int attributes_count, attribute_info[] attributes) {
        this.access_flags = access_flags;
        this.name_index = name_index;
        this.description_index = description_index;
        this.attributes_count = attributes_count;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "method_info{" +
                "access_flags=" + access_flags +
                ", name_index=" + name_index +
                ", description_index=" + description_index +
                ", attributes_count=" + attributes_count +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }
}
