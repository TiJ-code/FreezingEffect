package dk.tij.freezingEffect.constants;

public final class InterpolationFunctions {
    public static double smoothstep(double t) {
        return t * t * (3d - 2d * t);
    }

    public static double smootherstep(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min)
            return min;
        else if (value > max)
            return max;

        return value;
    }
}
