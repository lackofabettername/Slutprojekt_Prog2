package utility;

import java.io.Serializable;
import java.util.*;

public class RollingBuffer<T> extends AbstractList<T> implements Serializable {
    private final T[] array;
    private int pointer;
    private int size;

    public RollingBuffer(int arrayLength) {
        //noinspection unchecked
        array = (T[]) new Object[arrayLength];
    }

    public boolean set(T object) {
        array[pointer++] = object;
        pointer %= array.length;
        return true;
    }

    @Override
    public boolean add(T object) {
//        array[pointer++] = object;
//        pointer %= array.length;
//        return true;

        if (object == null) return false;

        for (int attempts = 0; attempts < array.length; attempts++) {
            int i = (pointer + attempts) % array.length;
            if(array[i] == null) {
                array[i] = object;
                pointer = (i+1) % array.length;
                return true;
            }
        }
        return false;
    }
    @Override
    public T get(int index) {
        return null;
    }
    @Override
    public void clear() {
        Arrays.fill(array, null);
        pointer = 0;
    }

    public void remove() {
        array[pointer] = null;
    }

    public T get() {
        return array[pointer];
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = (pointer + array.length) % array.length;
    }

    @Override
    public int size() {
        return array.length;
    }
    @Override
    public Object[] toArray() {
        return array;
    }
    @Override
    public <T1> T1[] toArray(T1[] a) {
        //System.arraycopy(array, pointer, a, 0, Math.min(array.length - pointer, a.length));
        //System.arraycopy(array, 0, a, array.length - pointer, Math.min(pointer, a.length));
        System.arraycopy(array, 0, a, 0, array.length);
        return a;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int pos = 0;
            @Override
            public boolean hasNext() {
                return pos < array.length;
            }
            @Override
            public T next() {
                return array[(pointer + pos++) % array.length];
            }
        };
    }

    public ListIterator<T> listIterator() {
        return new ListIterator<T>() {
            int pos;

            @Override
            public boolean hasNext() {
                return pos < array.length;
            }
            @Override
            public T next() {
                return array[(pointer + pos++) % array.length];
            }
            @Override
            public boolean hasPrevious() {
                return false;// TODO: 2021-05-14
            }
            @Override
            public T previous() {
                return null;// TODO: 2021-05-14
            }
            @Override
            public int nextIndex() {
                return 0;// TODO: 2021-05-14
            }
            @Override
            public int previousIndex() {
                return 0;// TODO: 2021-05-14
            }
            @Override
            public void remove() {
               set(null);
            }
            @Override
            public void set(T t) {
                //setPointer(pointer-1);
                array[(pointer + pos + array.length - 1) % array.length] = t;
            }
            @Override
            public void add(T t) {
                RollingBuffer.this.add(t);
            }
        };
    }
}
