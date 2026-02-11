package xyz.metratrj.jbyteinspector.parser.classfile;

public class CONSTANT_String_info extends cp_info {
    private final int string_index;

    public CONSTANT_String_info(int tag, int string_index) {
        super(tag);
        this.string_index = string_index;
    }

    public int getString_index() {
        return string_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_String_info{" +
                "string_index=" + string_index +
                ", tag=" + tag +
                '}';
    }
}
