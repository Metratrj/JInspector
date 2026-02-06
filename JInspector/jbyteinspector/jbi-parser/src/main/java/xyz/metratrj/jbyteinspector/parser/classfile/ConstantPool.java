package xyz.metratrj.jbyteinspector.parser.classfile;

import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unused")
public interface ConstantPool extends Iterable<PoolEntry> {
    PoolEntry entryByIndex(int index);

    int size();

    <T extends PoolEntry> T entryByIndex(int index, Class<T> cls);

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<PoolEntry> iterator() {
        return new Iterator<>() {
            int index = 1;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public PoolEntry next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                var e = entryByIndex(index);
                index += e.width();
                return e;
            }
        };
    }
}
