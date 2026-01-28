package xyz.metratrj.system;

public class cp_info {
    int tag;

    @Override
    public String toString() {
        return "cp_info{" +
                "tag=" + tag +
                '}';
    }

    public cp_info(int tag) {
        super();
        this.tag = tag;
    }
}
