package dk.tij.freezingEffect.constants;

public final class InterpolationFunctions {
    public static double linear(double t) {
        return t;
    }

    public static double smoothstep(double t) {
        return t * t * (3d - 2d * t);
    }

    public static double smootherstep(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
