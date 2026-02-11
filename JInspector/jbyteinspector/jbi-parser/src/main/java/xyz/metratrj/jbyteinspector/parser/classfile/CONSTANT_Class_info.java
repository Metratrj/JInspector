package xyz.metratrj.jbyteinspector.parser.classfile;

public class CONSTANT_Class_info extends cp_info {
    public int name_index;

    public CONSTANT_Class_info(int tag, int name_index) {
        super(tag);
        this.name_index = name_index;
    }

    public int getName_index() {
        return name_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_Class_info{" +
                "name_index=" + name_index +
                ", tag=" + tag +
                '}';
    }
}
