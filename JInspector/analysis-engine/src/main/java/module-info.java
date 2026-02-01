module xyz.metratrj.jbyteinspector.analysis {
    requires transitive xyz.metratrj.jbyteinspector.api;
    requires xyz.metratrj.jbyteinspector.parser;
    requires xyz.metratrj.jbyteinspector.core;
    requires java.logging;
    exports xyz.metratrj.jbyteinspector.analysis;
}