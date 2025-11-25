package dk.tij.freezingEffect.constants;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class TemperatureConstants {
    public static double
        DEFAULT_TEMPERATURE = 40,
        TEMPERATURE_DECAY = 1.0,
        FREEZING_THRESHOLD = 20.0,
        CRITICAL_FREEZING_THRESHOLD = 10.0;
    public static int
        HEAT_RADIUS = 5;

    public static final Set<Material> HEAT_SOURCES = new HashSet<>();
}
