package dk.tij.freezingEffect.constants;

public final class InterpolationFunctions {
    public static double linear(double t) {
        return t;
    }

    public static double smoothstep(double t) {
        int integerT = (int)t;
        t -= integerT;
        return t * t * (3d - 2d * t) + integerT;
    }

    public static double smootherstep(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static int clampI(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clampD(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
