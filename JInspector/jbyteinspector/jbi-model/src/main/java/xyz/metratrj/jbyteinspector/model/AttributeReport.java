package xyz.metratrj.jbyteinspector.model;

import java.util.Arrays;
import java.util.Objects;

public record AttributeReport(
        String name,
        int length,
        byte[] data
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeReport that = (AttributeReport) o;
        return length == that.length && Objects.equals(name, that.name) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, length);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
