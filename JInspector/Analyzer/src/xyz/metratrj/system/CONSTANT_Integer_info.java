package xyz.metratrj.system;

public class CONSTANT_Integer_info extends cp_info {
    int bytes; // big endian

    public CONSTANT_Integer_info(int tag, int bytes) {
        super(tag);
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "CONSTANT_Integer_info{" +
                "bytes=" + bytes +
                ", tag=" + tag +
                '}';
    }
}
