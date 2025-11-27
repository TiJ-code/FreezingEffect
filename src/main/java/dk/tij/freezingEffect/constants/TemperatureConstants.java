package dk.tij.freezingEffect.constants;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class TemperatureConstants {
    public static int
        CRITICAL_FREEZING_TICKS = 1800,
        HEAT_RADIUS = 5;

    public static final Set<Material> HEAT_SOURCES = new HashSet<>();

    public static final int
            VANILLA_MIN_FREEZE_TICKS = 0,
            VANILLA_MAX_FREEZE_TICKS = 140;
}
