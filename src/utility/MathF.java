package utility;

public class MathF {
    public static final float GoldenRatio = 1.61803398875f;

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float bounce(float value, float lower, float upper) {
        float n = upper - lower;
        float x = value - lower;
        x = n - Math.abs(Math.abs(x) % (2 * n) - n);
        return x + lower;
    }

    public static float map(float t, float lowerIn, float upperIn, float lowerOut, float upperOut) {
        return (t - lowerIn) / (upperIn - lowerIn) * (upperOut - lowerOut) + lowerOut;
    }

    public static float lerp(float t, float start, float end) {
        return end + t * (start - end);
    }
    public static int lerp(float t, int start, int end) {
        return Math.round(end + t * (start - end));
    }

    public static float max(float... values) {
        float max = values[0];
        for (int i = 1; i < values.length; ++i) {
            max = Math.max(max, values[i]);
        }
        return max;
    }

    public static float min(float... values) {
        float min = values[0];
        for (int i = 1; i < values.length; ++i) {
            min = Math.min(min, values[i]);
        }
        return min;
    }

    public static int random(int lower, int upper) {
        return lower + (int) (Math.random() * (upper - lower));
    }

    public static boolean inRange(float value, float min, float max) {
        return value > min && value < max;
    }
}
