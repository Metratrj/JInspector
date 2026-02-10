package xyz.metratrj.jbyteinspector.model;

/**
 * Represents a position/offset within a method's bytecode.
 */
public final class Label {
    private int offset = -1;
    private final String name;

    public Label() {
        this.name = null;
    }

    public Label(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        if (this.offset != -1) {
            throw new IllegalStateException("Label offset already set");
        }
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name != null ? name : "L" + offset;
    }
}
