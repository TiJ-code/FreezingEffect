package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.utils.HeatSource;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.utils.ItemUtils;
import dk.tij.freezingEffect.utils.Maths;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourceHandler {
    private static final double TO_PERCENT_CONVERSION_FACTOR = 1d / 100d;

    private final FileConfiguration config;

    public ResourceHandler(JavaPlugin plugin) {
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        loadFrostPlayerStats();
        loadIsolationValues();
        loadHeatSourceValues();
        loadInterpolationFunction();
    }

    private void loadFrostPlayerStats() {
        TemperatureConstants.CRITICAL_FREEZING_TICKS = Maths.clampPositiveI(
                config.getInt("frost.criticalFreezingTicks", Integer.MAX_VALUE)
        );
        TemperatureConstants.HEAT_RADIUS = Maths.clampPositiveIntD(
                config.getDouble("frost.heatRadius", 0)
        );
        TemperatureConstants.HEAT_RADIUS_SQUARED = TemperatureConstants.HEAT_RADIUS * TemperatureConstants.HEAT_RADIUS;
    }

    private void loadIsolationValues() {
        ConfigurationSection isolationSection = config.getConfigurationSection("frost.isolation");

        if (isolationSection == null) return;

        int maxPossibleIsolationValue = Maths.clampI0To100(isolationSection.getInt("maxPossibleIsolation", 0));
        TemperatureConstants.MAX_POSSIBLE_ISOLATION = maxPossibleIsolationValue * TO_PERCENT_CONVERSION_FACTOR;

        ConfigurationSection armourSection = isolationSection.getConfigurationSection("armourPieces");

        if (armourSection == null) return;

        for (String key : armourSection.getKeys(false)) {
            Material material = Material.getMaterial(key);

            if (!ItemUtils.isArmourItem(material)) continue;

            int value = Maths.clampI0To100(armourSection.getInt(key));
            TemperatureConstants.ARMOUR_PIECE_ISOLATION.put(material,
                    value * TO_PERCENT_CONVERSION_FACTOR);
        }
    }

    private void loadHeatSourceValues() {
        ConfigurationSection heatSourceSection = config.getConfigurationSection("frost.heatSources");

        if (heatSourceSection == null) return;

        for (String key : heatSourceSection.getKeys(false)) {
            Material material = Material.matchMaterial(key);

            if (material == null) continue;

            double value = Maths.clampPositiveIntD(heatSourceSection.getDouble(key + ".value"));
            double radius = Maths.clampPositiveIntD(heatSourceSection.getInt(key + ".radius"));
            TemperatureConstants.HEAT_SOURCE_WARMING.put(material, new HeatSource(value, radius*radius));
        }

        TemperatureConstants.PLAYER_BURNING_BOOST = Maths.clampI0To100(
                config.getInt("frost.playerBurningBoost", 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;
    }

    private void loadInterpolationFunction() {
        String interpolationFunctionName = config.getString("frost.interpolationFunction",
                TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        TemperatureConstants.INTERPOLATION_FUNCTION = TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }
}
