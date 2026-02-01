package xyz.metratrj.jbyteinspector.parser.classfile;

public class CONSTANT_Utf8_info extends cp_info {
    String value;

    public CONSTANT_Utf8_info(int tag, String value) {
        super(tag);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CONSTANT_Utf8_info{" +
                "value='" + value + '\'' +
                ", tag=" + tag +
                '}';
    }
}
