package dk.tij.winterweather.utils;

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
}
