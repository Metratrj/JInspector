package xyz.metratrj.jbyteinspector.parser.classfile;

@SuppressWarnings("unused")
public interface PoolEntry {

    ConstantPool constantPool();

    int tag();

    int index();

    int width();
}
