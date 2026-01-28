package xyz.metratrj.system;


public class CONSTANT_Double_info extends cp_info {
    double bytes;

    public CONSTANT_Double_info(int tag, double bytes) {
        super(tag);
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "CONSTANT_Double_info{" +
                "bytes=" + bytes +
                ", tag=" + tag +
                '}';
    }
}
