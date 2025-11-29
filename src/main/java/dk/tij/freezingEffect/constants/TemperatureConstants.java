package dk.tij.freezingEffect.constants;

import dk.tij.freezingEffect.utils.HeatSource;
import dk.tij.freezingEffect.utils.InterpolationFunctions;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class TemperatureConstants {
    public static final int
            VANILLA_DAMAGE_FREEZE_TICKS = 40,
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
            MAX_POSSIBLE_ISOLATION = 0.80,
            PLAYER_BURNING_BOOST = 1.10;

    public static final Map<Material, Double> ARMOUR_PIECE_ISOLATION = new HashMap<>();
    public static final Map<Material, HeatSource> HEAT_SOURCE_WARMING = new HashMap<>();

    public static Function<Double, Double> INTERPOLATION_FUNCTION = InterpolationFunctions::smootherstep;
}
