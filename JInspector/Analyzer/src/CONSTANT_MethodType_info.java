public class CONSTANT_MethodType_info extends cp_info {
    int description_index;

    public CONSTANT_MethodType_info(int tag, int description_index) {
        super(tag);
        this.description_index = description_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_MethodType_info{" +
                "description_index=" + description_index +
                ", tag=" + tag +
                '}';
    }
}
