public class CONSTANT_NameAndType_info extends cp_info {
    int name_index;
    int descriptor_index;

    public CONSTANT_NameAndType_info(int tag, int name_index, int descriptor_index) {
        super(tag);
        this.name_index = name_index;
        this.descriptor_index = descriptor_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_NameAndType_info{" +
                "descriptor_index=" + descriptor_index +
                ", tag=" + tag +
                ", name_index=" + name_index +
                '}';
    }
}
