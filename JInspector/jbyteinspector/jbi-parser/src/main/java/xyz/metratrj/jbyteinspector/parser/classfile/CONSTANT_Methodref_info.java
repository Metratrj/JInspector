package xyz.metratrj.jbyteinspector.parser.classfile;

public class CONSTANT_Methodref_info extends cp_info {
    int class_index;
    int name_and_type_index;

    public int getClass_index() {
        return class_index;
    }

    public int getName_and_type_index() {
        return name_and_type_index;
    }

    public CONSTANT_Methodref_info(int tag, int class_index, int name_and_type_index) {
        super(tag);
        this.class_index         = class_index;
        this.name_and_type_index = name_and_type_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_Methodref_info{" +
                "class_index=" + class_index +
                ", name_and_type_index=" + name_and_type_index +
                ", tag=" + tag +
                '}';
    }
}
