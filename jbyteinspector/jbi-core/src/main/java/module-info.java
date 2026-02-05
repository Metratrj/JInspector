module xyz.metratrj.jbyteinspector.core {
    requires transitive xyz.metratrj.jbyteinspector.model;
    requires xyz.metratrj.jbyteinspector.parser;
    requires xyz.metratrj.jbyteinspector.utils;
    requires xyz.metratrj.jbyteinspector.io;
    requires java.logging;
    exports xyz.metratrj.jbyteinspector.core;
}