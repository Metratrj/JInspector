package xyz.metratrj.jbyteinspector.cli;

/*
* cp_info {
    u1 tag;
    u1 info[];
}
* */
public abstract class CPInfo extends Entity{
    int Tag;
    byte[] Info;
}
