package utility;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Utility {

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static<T> T deserialize(byte[] data) throws IOException, ClassNotFoundException, ClassCastException {
        return deserialize(data, 0);
    }
    public static<T> T deserialize(byte[] data, int offset) throws IOException, ClassNotFoundException, ClassCastException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        in.skipNBytes(offset);
        ObjectInputStream is = new ObjectInputStream(in);
        //Problem here?
        try {
            return (T) is.readObject(); //TODO: This may be dangerous
        } catch (IOException | ClassNotFoundException e) {
            Debug.logError(e);
            throw e;
        }
    }

    /**
     * Returns the first value unless it's null, in which case it returns the second.
     * @param a Returned if it isn't null
     * @param b Returned if a is null
     * @return a unless it is null, in which case b is returned
     */
    public static <T> T orDefault(T a, T b) {
        return a != null ? a : b;
    }

    public static <T> void forEach(Collection<T> a, Collection<T> b, BiConsumer<T, T> function) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        if (a.size() != b.size())
            throw new IllegalArgumentException("The collections must have equal length");

        for (Iterator<T> iterA = a.iterator(), iterB = b.iterator(); iterA.hasNext();) {
            function.accept(iterA.next(), iterB.next());
        }
    }

    //region Collection-to-Array
    public static float[]   floatCollectionToArray  (Collection<Float> collection) {
        float[] result = new float[collection.size()];
        int i = 0;
        for (Float aFloat : collection)
            result[i++] = aFloat;
        return result;
    }

    public static double[]  doubleCollectionToArray (Collection<Double> collection) {
        return collection.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public static byte[]    byteCollectionToArray   (Collection<Byte> collection) {
        byte[] result = new byte[collection.size()];
        int i = 0;
        for (Byte aByte : collection) {
            result[i++] = aByte;
        }
        return result;
    }

    public static short[]   shortCollectionToArray  (Collection<Short> collection) {
        short[] result = new short[collection.size()];
        int i = 0;
        for (Short aShort : collection)
            result[i++] = aShort;
        return result;
    }

    public static int[]     intCollectionToArray    (Collection<Integer> collection) {
        return collection.stream().mapToInt(Integer::intValue).toArray();
    }

    public static long[]    longCollectionToArray   (Collection<Long> collection) {
        return collection.stream().mapToLong(Long::longValue).toArray();
    }

    public static char[]    charCollectionToArray   (Collection<Character> collection) {
        char[] result = new char[collection.size()];
        int i = 0;
        for (Character character : collection)
            result[i++] = character;
        return result;
    }

    public static boolean[] booleanCollectionToArray(Collection<Boolean> collection) {
        boolean[] result = new boolean[collection.size()];
        int i = 0;
        for (Boolean aBoolean : collection)
            result[i++] = aBoolean;
        return result;
    }
    //endregion

    //region Primitive-to-Wrapper
    public static Float[]     primitiveToWrapperArray(float[] array) {
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Double[]    primitiveToWrapperArray(double[] array) {
        Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Byte[]      primitiveToWrapperArray(byte[] array) {
        Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Short[]     primitiveToWrapperArray(short[] array) {
        Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Integer[]   primitiveToWrapperArray(int[] array) {
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Long[]      primitiveToWrapperArray(long[] array) {
        Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Character[] primitiveToWrapperArray(char[] array) {
        Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }

    public static Boolean[]   primitiveToWrapperArray(boolean[] array) {
        Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i)
            result[i] = array[i];
        return result;
    }
    //endregion
}
