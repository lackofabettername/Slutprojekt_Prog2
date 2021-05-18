package utility;



import utility.style.Background;
import utility.style.Font;
import utility.style.Foreground;
import utility.style.Style;

import java.util.*;

import static utility.Reversed.reversed;


@SuppressWarnings("UnusedReturnValue")
public class Debug {
    private static final Object _logLock = new Object();
    private static volatile int _offset;
    private static volatile boolean _isStartOfLine;
    private static volatile HashMap<Thread, Style[]> threadDecoration = new HashMap<>();

    public static void logAll(Object... message) {
        synchronized (_logLock) {
            for (int i = 0; i < message.length; i++) {
                Object part = message[i];
                log((part == null ? "NULL" : part.toString()) + (i < message.length - 1 ? ", " : "\n"), false);
            }
        }
    }

    public static void logError(Throwable error) {
        synchronized (_logLock) {
            logError(error.toString().trim());
            offsetOutput(1);
            for (StackTraceElement element : error.getStackTrace())
                logDecorated("at " + element.toString(), Foreground.Red);
            offsetOutput(-1);
        }
    }
    public static void logError(String text) {
        logDecorated(text, Foreground.Red);
    }
    public static void logError(String text, boolean newLine) {
        logDecorated(text, newLine, Foreground.Red);
    }

    public static void logWarning(String text) {
        logDecorated(text, Foreground.DarkYellow);
    }
    public static void logWarning(String text, boolean newLine) {
        logDecorated(text, newLine, Foreground.DarkYellow);
    }

    public static void logNamed(String text) {
        logNamedProper(text, true);
    }
    public static void logNamed(String text, boolean newLine) {
        logNamedProper(text, newLine);
    }
    private static void logNamedProper(String text, boolean newLine) {
        synchronized (_logLock) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            log(stackTrace[3].getClassName(), false);
            log(": ", false);
            log(text, true);
        }
    }
    public static void logNamedShort(String text) {
        logNamedShortProper(text, true);
    }
    public static void logNamedShort(String text, boolean newLine) {
        logNamedShortProper(text, newLine);
    }
    private static void logNamedShortProper(String text, boolean newLine) {
        synchronized (_logLock) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            log(stackTrace[3].getClassName().replaceAll(".*\\.", ""), false);
            log(": ", false);
            log(text, true);
        }
    }

    public static <T> void logArrayCompare(T[] a, T[] b) {
        int longest = 3;
        for (T obj : a)
            longest = Math.max(longest, obj.toString().length());
        for (T obj : b)
            longest = Math.max(longest, obj.toString().length());


        synchronized (_logLock) {
            for (int i = 0, stop = Math.max(a.length, b.length); i < stop; ++i) {
                log(String.format("%" + ((int) Math.log10(stop) + 1) + "d ", i), false);
                logDecorated("|", false, i < a.length && i < b.length && a[i].equals(b[i]) ? Foreground.Green : Foreground.Red);

                log(String.format(" %" + longest + "s, ", i < a.length ? a[i] : "-".repeat(longest - 2) + " "), false);
                log(String.format("%" + longest + "s", i < b.length ? b[i] : "-".repeat(longest - 2) + " "), true);
            }
        }
    }
    //fixme: this vararg can cause heap pollution     https://www.geeksforgeeks.org/what-is-heap-pollution-in-java-and-how-to-resolve-it/
    public static <T> void logArrayCompare(T[]... arrays) {
        int printSize = 3;
        int length = 0;

        for (T[] array : arrays) {
            for (T obj : array) {
                printSize = Math.max(printSize, obj.toString().length());
            }
            length = Math.max(length, array.length);
        }


        String indexPadding = "%" + ((int) Math.log10(length) + 1) + "d";
        String objectPadding = "%" + printSize + "s";
        ArrayList<Style> styles = new ArrayList<>(EnumSet.complementOf(EnumSet.of(Foreground.Black, Foreground.LightGrey, Foreground.DarkGrey)));
        Collections.shuffle(styles);
        synchronized (_logLock) {
            for (int i = 0; i < length; ++i) {
                HashMap<String, Style> groups = new HashMap<>();

                log(String.format(indexPadding, +i) + " |", false);

                for (int j = 0; j < arrays.length; j++) {
                    T[] array = arrays[j];

                    String obj;
                    if (i < array.length)
                        obj = array[i].toString();
                    else
                        obj = "-".repeat(printSize - 1);

                    Style style;
                    if (groups.containsKey(obj)) {
                        style = groups.get(obj);
                    } else {
                        //style = Foreground.values()[groups.size()+1];
                        style = styles.get(groups.size());
                        groups.put(obj, style);
                    }

                    logDecorated(String.format(objectPadding, obj), false, style);
                    if (j < arrays.length - 1)
                        log(" ", false);
                }

                log("");
            }
        }
    }
    public static <T extends List<?>> void logListCompare(T a, T b) {
        logArrayCompare(a.toArray(), b.toArray());
    }

    public static void logDecorated(String text, Style... styles) {
        logDecorated(text, true, styles);
    }
    public static void logDecorated(String text, boolean newLine, Style... styles) {
        synchronized (_logLock) {

            boolean foreground = false;
            boolean background = false;

            for (Style style : styles) {

                if (style instanceof Foreground) {
                    if (foreground) {
                        logWarning("There should only be one foreground modifier");
                    } else {
                        foreground = true;
                    }
                } else if (style instanceof Background) {
                    if (background) {
                        logWarning("There should only be one background modifier");
                    } else {
                        background = true;
                    }
                }

                log(style.getCode(), false);
            }

            log(text, false);
            log((char) 27 + "[0m", newLine);// Clear style
        }
    }


    public static void logLine() {
        logLine(15);
    }
    public static void logLine(Foreground color) {
        logLine(15, color);
    }
    public static void logLine(int length) {
        logDecorated("          ".repeat(length), true, Font.StrikeThrough);
    }
    public static void logLine(int length, Foreground color) {
        logDecorated("          ".repeat(length), true, Font.StrikeThrough, color);
    }


    public static <T> T log(T object) {
        log(object != null ? object.toString() : "null", true);
        return object;
    }
    public static String log(String text) {
        return log(text, true);
    }
    public static <T> T log(T object, boolean newLine) {
        log(object.toString(), newLine);
        return object;
    }
    public static String log(String text, boolean newLine) {
        synchronized (_logLock) {
            if (_isStartOfLine)
                System.out.print("\t".repeat(_offset));

            if (!threadDecoration.isEmpty()) {
                for (Style style : threadDecoration.getOrDefault(Thread.currentThread(), new Style[0])) {
                    System.out.print(style.getCode());
                }

            }
            System.out.print(text + (newLine ? "\n" : ""));

            _isStartOfLine = newLine;
        }
        return text;
    }



    public static int offsetOutput(int amount) {
        synchronized (_logLock) {
            return _offset = Math.max(_offset + amount, 0);
        }
    }
    public static int setOutputOffset(int value) {
        synchronized (_logLock) {
            return _offset = Math.max(value, 0);
        }
    }



    public static void decorateThreadOutput(Thread thread, Style... style) {
        threadDecoration.put(thread, style);
    }



    public static String getCallStack() {
        StringBuilder oup = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int i = 0;
        for (StackTraceElement ste : reversed(stackTrace)) {
            if (i++ < stackTrace.length - 2)
                oup
                        .append(ste.getClassName())
                        .append(": ")
                        .append(ste.getMethodName())
                        .append(i < stackTrace.length - 2 ? " -> " : "");
        }

        return oup.toString();
    }
    public static String getCallStackSimplified() {
        StringBuilder oup = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int i = 0;
        for (StackTraceElement ste : reversed(stackTrace)) {
            if (i++ < stackTrace.length - 2)
                oup
                        .append(ste.getMethodName())
                        .append(i < stackTrace.length - 2 ? " -> " : "");
        }

        return oup.toString();
    }


    public static synchronized String intToString(int number, int byteCount) {//lack: Integer.toBinaryString()?

        StringBuilder result = new StringBuilder();

        for (int i = byteCount * 8 - 1; i >= 0; i--) {
            int mask = 1 << i;
            result.append((number & mask) != 0 ? "1" : "0");
            if (i % 4 == 0) {
                result.append(" ");
            }
        }

        result.replace(result.length() - 1, result.length(), "");
        return result.toString();
    }

    public static synchronized String joinStrings(Object... strings) {
        StringBuilder result = new StringBuilder();
        for (Object part : strings) {
            result.append(part);
        }
        return result.toString();
    }

    public static <T> String arrayCompare(T[] a, T[] b) {
        int longest = 3;
        for (T obj : a)
            longest = Math.max(longest, obj != null ? obj.toString().length() : 4);
        for (T obj : b)
            longest = Math.max(longest, obj != null ? obj.toString().length() : 4);


        StringBuilder output = new StringBuilder();
        for (int i = 0, stop = Math.max(a.length, b.length); i < stop; ++i) {
            output.append(String.format("%" + ((int) Math.log10(stop) + 1) + "d ", i))
                    .append("|")
                    .append(String.format(" %" + longest + "s, ", i < a.length ? a[i] : "-".repeat(longest - 2) + " "))
                    .append(String.format("%" + longest + "s", i < b.length ? b[i] : "-".repeat(longest - 2) + " "))
                    .append("\n");
        }
        return output.toString();
    }
    public static <T extends Collection<?>> String collectionCompare(T a, T b) {
        return arrayCompare(a.toArray(), b.toArray());
    }
    //fixme: this vararg can cause heap pollution     https://www.geeksforgeeks.org/what-is-heap-pollution-in-java-and-how-to-resolve-it/
    public static <T> String arrayCompare(T[]... arrays) {
        int printSize = 3;
        int length = 0;

        for (T[] array : arrays) {
            for (T obj : array) {
                printSize = Math.max(printSize, obj.toString().length());
            }
            length = Math.max(length, array.length);
        }


        StringBuilder output = new StringBuilder();

        String indexPadding = "%" + ((int) Math.log10(length) + 1) + "d";
        String objectPadding = "%" + printSize + "s";
        for (int i = 0; i < length; ++i) {
            output.append(String.format(indexPadding, +i)).append(" |");

            for (int j = 0; j < arrays.length; j++) {
                T[] array = arrays[j];

                String obj;
                if (i < array.length)
                    obj = array[i].toString();
                else
                    obj = "-".repeat(printSize - 1);


                output.append(String.format(objectPadding, obj));
                if (j < arrays.length - 1)
                    output.append(" ");
            }

            output.append("\n");
        }

        return output.toString();
    }

    public static void logAllStyleAlternatives() {
        for (Style foreground : Foreground.values()) {
            logDecorated(foreground.toString(), false, foreground);
            log(" ", false);
        }

        log("");

        for (Style background : Background.values()) {
            logDecorated(background.toString(), false, background);
            log(" ", false);
        }

        log("");

        for (Style font : Font.values()) {
            logDecorated(font.toString(), false, font);
            log(" ", false);
        }

        log("");
    }

    //region Test
    public static void main(String[] args) {
        //https://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html

        try {
            foo1();
        } catch (Throwable e) {
            Debug.logError(e);
            e.printStackTrace();
        }

        logAllStyleAlternatives();

        log("");
        logLine(Foreground.Cyan);
        log("");

        logNamed("hello");
        logNamedShort("hello");


        for (int i = 0; i < 128; ++i) {
            String temp = "" + i;
            if (temp.length() == 1)
                temp = "  " + temp + "  ";
            else if (temp.length() == 2)
                temp = "  " + temp + " ";
            else
                temp = "  " + temp + "";

            log((char) 27 + ("[" + i + "m") + temp + (char) 27 + "[0m", (i + 1) % 32 == 0);
        }

        for (int i = 0; i < 128; ++i) {
            String temp = "" + i;
            if (temp.length() == 1)
                temp = "  " + temp + "  ";
            else if (temp.length() == 2)
                temp = "  " + temp + " ";
            else
                temp = "  " + temp + "";

            log((char) 27 + ("[" + i + ";1m") + temp + (char) 27 + "[0m", (i + 1) % 32 == 0);
        }

        log((char) 27 + "[93m" + "A" + (char) 27 + "[93;1m" + "A" + (char) 27 + "[0m");
        log((char) 27 + "[33m" + "A" + (char) 27 + "[93m" + "A" + (char) 27 + "[0m");

        log((char) 27 + "[30m" + (char) 27 + "[103m" + "test" + (char) 27 + "[0m");
        log((char) 27 + "[30m" + (char) 27 + "[103;1m" + "test" + (char) 27 + "[0m");


        Debug.logDecorated("test", Foreground.Red);
        Debug.logDecorated("test", Foreground.Green, Background.Cyan, Font.Boxed);
        Debug.logDecorated("test", Font.Underline);


        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int[] b = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] c = new int[11];
        for (int i = 0; i < c.length; ++i)
            c[i] = (int) (Math.random() * 10) + 1;
        int[] d = new int[11];
        for (int i = 0; i < d.length; ++i)
            d[i] = (int) (Math.pow(Math.random(), 2) * 10) + 1;
        logArrayCompare(Utility.primitiveToWrapperArray(a), Utility.primitiveToWrapperArray(b), Utility.primitiveToWrapperArray(c), Utility.primitiveToWrapperArray(d));
    }
    static void foo1() {
        foo2();
    }
    static void foo2() {
        foo3();
    }
    static void foo3() {
        logDecorated(getCallStack(), Foreground.Green);
        logDecorated(getCallStackSimplified(), Foreground.Green);
        throw new IllegalArgumentException("sdf");
    }
    //endregion
}
