package xyz.metratrj.jbyteinspector.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record CodeReport(
        int maxStack,
        int maxLocals,
        int codeLength,
        byte[] code,
        List<InstructionReport> instructions,
        int exceptionTableLength,
        ExceptionTableEntry[] exceptionTable,
        int attributesCount,
        AttributeReport[] attributes
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeReport that = (CodeReport) o;
        return maxStack == that.maxStack && maxLocals == that.maxLocals && codeLength == that.codeLength && exceptionTableLength == that.exceptionTableLength && attributesCount == that.attributesCount && Arrays.equals(code, that.code) && Objects.equals(instructions, that.instructions) && Arrays.equals(exceptionTable, that.exceptionTable) && Arrays.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(maxStack, maxLocals, codeLength, instructions, exceptionTableLength, attributesCount);
        result = 31 * result + Arrays.hashCode(code);
        result = 31 * result + Arrays.hashCode(exceptionTable);
        result = 31 * result + Arrays.hashCode(attributes);
        return result;
    }
}
