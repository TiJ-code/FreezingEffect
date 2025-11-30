package dk.tij.winterweather.utils;

public final class Maths {
    public static final double TO_PERCENT_CONVERSION_FACTOR = 1d / 100d;

    public static int clampI(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clampPositiveI(int value) {
        return clampI(value, 0, Integer.MAX_VALUE);
    }

    public static int clampI0To100(int value) {
        return clampI(value, 0, 100);
    }

    public static double clampD(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clampPositiveIntD(double value) {
        return clampD(value, 0, Integer.MAX_VALUE);
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
