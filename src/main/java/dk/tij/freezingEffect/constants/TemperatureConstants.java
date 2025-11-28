package dk.tij.freezingEffect.constants;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class TemperatureConstants {
    public static final int
            VANILLA_MIN_FREEZE_TICKS = 0,
            VANILLA_MAX_FREEZE_TICKS = 140;

    public static final Map<String, Function<Double, Double>> INTERPOLATION_FUNCTIONS_MAPPING = Map.of(
            "linear", InterpolationFunctions::linear,
            "smoothstep", InterpolationFunctions::smoothstep,
            "smootherstep", InterpolationFunctions::smootherstep
    );

    public static int
        CRITICAL_FREEZING_TICKS = 1800,
        HEAT_RADIUS = 5;

    public static double
            LEATHER_ARMOUR_MAX_REDUCTION = 0.80,
            LEATHER_BOOTS_REDUCTION = 0.15,
            LEATHER_LEGGINGS_REDUCTION = 0.30,
            LEATHER_CHESTPLATE_REDUCTION = 0.40,
            LEATHER_HELMET_REDUCTION = 15;

    public static Function<Double, Double> INTERPOLATION_FUNCTION = InterpolationFunctions::smootherstep;

    public static final Set<Material> HEAT_SOURCES = new HashSet<>();
}
