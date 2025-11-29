package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.FreezingEffect;
import dk.tij.freezingEffect.utils.HeatSource;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.utils.ItemUtils;
import dk.tij.freezingEffect.utils.Maths;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import static dk.tij.freezingEffect.utils.Maths.TO_PERCENT_CONVERSION_FACTOR;

public class ResourceHandler {
    private final FreezingEffect plugin;
    private FileConfiguration config;

    public ResourceHandler(FreezingEffect plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public void loadConfig() {
        this.config = plugin.getConfig();
        loadFrostPlayerStats();
        loadIsolationValues();
        loadHeatSourceValues();
        loadInterpolationFunction();
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void setDebug(boolean debug) {
        config.set("frost.debug", debug);
        saveConfig();
    }

    public boolean isDebug() {
        return config.getBoolean("frost.debug");
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

        TemperatureConstants.PLAYER_POWDER_SNOW_BOOST = Maths.clampPositiveI(
                config.getInt("frost.playerPowderSnowBoost", 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;
    }

    private void loadInterpolationFunction() {
        String interpolationFunctionName = config.getString("frost.interpolationFunction",
                TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        TemperatureConstants.INTERPOLATION_FUNCTION = TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }
}
