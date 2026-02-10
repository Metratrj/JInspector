package xyz.metratrj.jbyteinspector.parser.classfile;

public class cp_info {
    public int tag;

    public cp_info(int tag) {
        super();
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "cp_info{" +
                "tag=" + tag +
                '}';
    }
}
