package xyz.metratrj.system;

public class CONSTANT_String_info extends cp_info{
    int string_index;

    public CONSTANT_String_info(int tag, int string_index) {
        super(tag);
        this.string_index = string_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_String_info{" +
                "tag=" + tag +
                ", string_index=" + string_index +
                '}';
    }
}
