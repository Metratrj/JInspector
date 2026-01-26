public class CONSTANT_MethodHandle_info extends cp_info {
    int reference_type;
    int reference_index;

    public CONSTANT_MethodHandle_info(int tag, int reference_type, int reference_index) {
        super(tag);
        this.reference_type = reference_type;
        this.reference_index = reference_index;
    }


    @Override
    public String toString() {
        return "CONSTANT_MethodHandle_info{" +
                "reference_index=" + reference_index +
                ", tag=" + tag +
                ", reference_type=" + reference_type +
                '}';
    }

}
