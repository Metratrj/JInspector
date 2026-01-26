public class CONSTANT_Methodref_info extends cp_info{
    int class_index;
    int name_and_type_index;

    @Override
    public String toString() {
        return "CONSTANT_Methodref_info{" +
                "class_index=" + class_index +
                ", name_and_type_index=" + name_and_type_index +
                ", tag=" + tag +
                '}';
    }

    public CONSTANT_Methodref_info(int tag, int class_index, int name_and_type_index) {
        super(tag);
        this.class_index = class_index;
        this.name_and_type_index = name_and_type_index;
    }
}
