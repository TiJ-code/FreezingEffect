package dk.tij.winterweather.utils;

import java.util.Map;
import java.util.function.Function;

public final class InterpolationFunctions {
    public static final Map<String, Function<Double, Double>> INTERPOLATION_FUNCTIONS_MAPPING = Map.of(
            "linear", InterpolationFunctions::linear,
            "smoothstep", InterpolationFunctions::smoothstep,
            "smootherstep", InterpolationFunctions::smootherstep
    );


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
