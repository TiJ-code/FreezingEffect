package dk.tij.freezingEffect.constants;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class TemperatureConstants {
    public static double
        TEMPERATURE_DECAY = 1.0,
        FREEZING_DAMAGE_THRESHOLD = 10.0;
    public static int
        HEAT_RADIUS = 5;

    public static final int
        VANILLA_MAX_FREEZE_TICKS = 140;

    public static final Set<Material> HEAT_SOURCES = new HashSet<>();
}
