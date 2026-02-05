package xyz.metratrj.jbyteinspector.parser.classfile;


import java.util.List;
import java.util.Set;

public record ClassReport(
        String className,
        String superClassName,
        Set<String> flags,
        cp_info[] constantPool,
        List<MethodReport> methods,
        List<FieldReport> fields
) {
    public cp_info getConstantPoolItem(int index) {
        if (constantPool != null && index > 0 && index < constantPool.length) {
            return constantPool[index];
        }
        return null;
    }

    public <T extends cp_info> T getConstantPoolItem(int index, Class<T> type) {
        cp_info item = getConstantPoolItem(index);
        if (type.isInstance(item)) {
            return type.cast(item);
        }
        return null;
    }

    public int getConstantPoolTag(int index) {
        cp_info item = getConstantPoolItem(index);
        if (item != null) {
            return item.tag;
        }
        return 0;
    }
}
