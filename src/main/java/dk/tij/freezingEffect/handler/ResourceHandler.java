package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.stream.Collectors;

public class ResourceHandler {
    private static final double TO_PERCENT_CONVERSION_FACTOR = 1d / 100d;

    private final FileConfiguration config;

    public ResourceHandler(JavaPlugin plugin) {
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        TemperatureConstants.LEATHER_ARMOUR_MAX_REDUCTION = InterpolationFunctions.clampI(
                config.getInt("frost.leatherArmourMaxReduction", 0),
                0, 100
        ) * TO_PERCENT_CONVERSION_FACTOR;
        TemperatureConstants.LEATHER_BOOTS_REDUCTION = InterpolationFunctions.clampI(
                config.getInt("frost.leatherBootsReduction", 0),
                0, 100
        ) * TO_PERCENT_CONVERSION_FACTOR;
        TemperatureConstants.LEATHER_LEGGINGS_REDUCTION = InterpolationFunctions.clampI(
                config.getInt("frost.leatherLeggingsReduction", 0),
                0, 100
        ) * TO_PERCENT_CONVERSION_FACTOR;
        TemperatureConstants.LEATHER_CHESTPLATE_REDUCTION = InterpolationFunctions.clampI(
                config.getInt("frost.leatherChestplateReduction", 0),
                0, 100
        ) * TO_PERCENT_CONVERSION_FACTOR;
        TemperatureConstants.LEATHER_HELMET_REDUCTION = InterpolationFunctions.clampI(
                config.getInt("frost.leatherHelmetReduction", 0),
                0, 100
        ) * TO_PERCENT_CONVERSION_FACTOR;

        TemperatureConstants.CRITICAL_FREEZING_TICKS = config.getInt("frost.criticalFreezingTicks", Integer.MAX_VALUE);
        TemperatureConstants.HEAT_RADIUS = config.getInt("frost.heatRadius", 0);

        Set<Material> configHeatSources = config.getStringList("frost.heatSources")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
        TemperatureConstants.HEAT_SOURCES.addAll(configHeatSources);

        String interpolationFunctionName = config.getString("frost.interpolationFunction",
                TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        TemperatureConstants.INTERPOLATION_FUNCTION = TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }
}
