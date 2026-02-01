package xyz.metratrj.jbyteinspector.parser.classfile;


public class CONSTANT_Float_info extends cp_info {
    float bytes;

    public CONSTANT_Float_info(int tag, float bytes) {
        super(tag);
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "CONSTANT_Float_info{" +
                "bytes=" + bytes +
                ", tag=" + tag +
                '}';
    }
}
