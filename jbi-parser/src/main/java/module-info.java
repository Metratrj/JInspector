module xyz.metratrj.jbyteinspector.parser {
    requires xyz.metratrj.jbyteinspector.model;
    requires xyz.metratrj.jbyteinspector.utils;
    requires java.logging;
    exports xyz.metratrj.jbyteinspector.parser;
    exports xyz.metratrj.jbyteinspector.parser.classfile;
    exports xyz.metratrj.jbyteinspector.parser.model;
    exports xyz.metratrj.jbyteinspector.parser.utils;
}