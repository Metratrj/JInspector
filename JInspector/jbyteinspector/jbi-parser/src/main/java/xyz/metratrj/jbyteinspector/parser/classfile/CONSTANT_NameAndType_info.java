package xyz.metratrj.jbyteinspector.parser.classfile;

public class CONSTANT_NameAndType_info extends cp_info {
    private final int name_index;
    private final int descriptor_index;

    public CONSTANT_NameAndType_info(int tag, int name_index, int descriptor_index) {
        super(tag);
        this.name_index       = name_index;
        this.descriptor_index = descriptor_index;
    }

    public int getName_index() {
        return name_index;
    }

    public int getDescriptor_index() {
        return descriptor_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_NameAndType_info{" +
                "name_index=" + name_index +
                ", descriptor_index=" + descriptor_index +
                ", tag=" + tag +
                '}';
    }
}
