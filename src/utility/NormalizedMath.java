package utility;

/**
 * https://youtu.be/mr5xkf6zSzk
 */
public class NormalizedMath {//TODO: rename
    public static float mix(float a, float b, float t) {
        return a + t * (b - a);
    }

    public static float flip(float x) {
        return 1 - x;
    }

    public static float smoothStart2(float x) {
        return x * x;
    }
    public static float smoothStart3(float x) {
        return x * x * x;
    }
    public static float smoothStart4(float x) {
        x *= x;
        return x * x;
    }
    public static float smoothStart5(float x) {
        float a = x * x;
        return a * a * x;
    }
    public static float smoothStart6(float x) {
        x *= x;
        return x * x * x;
    }

    public static float smoothStop2(float x) {
        return 1 - smoothStart2(1 - x);
    }
    public static float smoothStop3(float x) {
        return 1 - smoothStart3(1 - x);
    }
    public static float smoothStop4(float x) {
        return 1 - smoothStart4(1 - x);
    }
    public static float smoothStop5(float x) {
        return 1 - smoothStart5(1 - x);
    }
    public static float smoothStop6(float x) {
        return 1 - smoothStart6(1 - x);
    }

    public static float smoothStep2(float x) {
        return mix(smoothStart2(x), smoothStop2(x), x);
    }
    public static float smoothStep3(float x) {
        return mix(smoothStart3(x), smoothStop3(x), x);
    }
    public static float smoothStep4(float x) {
        return mix(smoothStart4(x), smoothStop4(x), x);
    }
    public static float smoothStep5(float x) {
        return mix(smoothStart5(x), smoothStop5(x), x);
    }
    public static float smoothStep6(float x) {
        return mix(smoothStart6(x), smoothStop6(x), x);
    }

}
