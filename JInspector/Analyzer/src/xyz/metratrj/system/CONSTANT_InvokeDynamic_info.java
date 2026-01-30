package xyz.metratrj.system;

public class CONSTANT_InvokeDynamic_info extends cp_info {
    int bootstrap_method_attr_index;
    int name_and_type_index;

    public CONSTANT_InvokeDynamic_info(int tag, int bootstrap_method_attr_index, int name_and_type_index) {
        super(tag);
        this.bootstrap_method_attr_index = bootstrap_method_attr_index;
        this.name_and_type_index         = name_and_type_index;
    }

    @Override
    public String toString() {
        return "CONSTANT_InvokeDynamic_info{" +
                "bootstrap_method_attr_index=" + bootstrap_method_attr_index +
                ", name_and_type_index=" + name_and_type_index +
                ", tag=" + tag +
                '}';
    }
}
