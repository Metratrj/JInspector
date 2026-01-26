public class CONSTANT_Long_info extends cp_info {
    long value;
    public CONSTANT_Long_info(int tag, long bytes) {
        super(tag);
        value = bytes;
    }


    @Override
    public String toString() {
        return "CONSTANT_Long_info{" +
                "value=" + value +
                ", tag=" + tag +
                '}';
    }
}
