package dk.tij.winterweather.handler;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.ConfigEntries;
import dk.tij.winterweather.config.ConfigReader;
import dk.tij.winterweather.utils.HeatSource;
import dk.tij.winterweather.constants.TemperatureConstants;
import dk.tij.winterweather.utils.ItemUtils;
import dk.tij.winterweather.utils.Maths;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import static dk.tij.winterweather.utils.Maths.TO_PERCENT_CONVERSION_FACTOR;

public class ResourceHandler {
    private final WinterWeather plugin;
    private final ConfigReader reader;

    public ResourceHandler(WinterWeather plugin) {
        this.plugin = plugin;
        this.reader = new ConfigReader(plugin);

        loadConfig();
    }

    public void reloadConfig() {
        reader.reloadConfig();
    }

    public void setEnabled(boolean enabled) {
        plugin.getConfig().set(ConfigEntries.ENABLED, enabled);
        plugin.saveConfig();
    }

    public boolean isEnabled() {
        return reader.getBoolean(ConfigEntries.ENABLED, false);
    }

    private void loadConfig() {
        loadFrostPlayerStats();
        loadIsolationValues();
        loadHeatSourceValues();
        loadInterpolationFunction();
    }

    private void loadFrostPlayerStats() {
        TemperatureConstants.CRITICAL_FREEZING_TICKS = Maths.clampPositiveI(
                reader.getInt(ConfigEntries.CRITICAL_FREEZING_TICKS, Integer.MAX_VALUE)
        );
        TemperatureConstants.PLAYER_RADIUS = Maths.clampPositiveIntD(
                reader.getDouble(ConfigEntries.PLAYER_RADIUS, 0)
        );
        TemperatureConstants.PLAYER_RADIUS_SQUARED = TemperatureConstants.PLAYER_RADIUS * TemperatureConstants.PLAYER_RADIUS;
    }

    private void loadIsolationValues() {
        ConfigurationSection isolationSection = plugin.getConfig().getConfigurationSection(ConfigEntries.SUB_CATEGORY_ISOLATION);

        if (isolationSection == null) return;

        int maxPossibleIsolationValue = Maths.clampI0To100(isolationSection.getInt(ConfigEntries.ISOLATION_MAX_POSSIBLE_ISOLATION, 0));
        TemperatureConstants.MAX_POSSIBLE_ISOLATION = maxPossibleIsolationValue * TO_PERCENT_CONVERSION_FACTOR;

        ConfigurationSection armourSection = isolationSection.getConfigurationSection(ConfigEntries.ISOLATION_ARMOUR_PIECES);

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
        ConfigurationSection heatSourceSection = plugin.getConfig().getConfigurationSection(ConfigEntries.SUB_CATEGORY_HEAT_SOURCES);

        if (heatSourceSection == null) return;

        for (String key : heatSourceSection.getKeys(false)) {
            Material material = Material.matchMaterial(key);

            if (material == null) continue;

            double value = Maths.clampPositiveIntD(heatSourceSection.getDouble(key + ConfigEntries.HEAT_SOURCE_VALUE));
            double radius = Maths.clampPositiveIntD(heatSourceSection.getInt(key + ConfigEntries.HEAT_SOURCE_RADIUS));
            TemperatureConstants.HEAT_SOURCE_WARMING.put(material, new HeatSource(value, radius*radius));
        }

        TemperatureConstants.PLAYER_BURNING_BOOST = Maths.clampI0To100(
                reader.getInt(ConfigEntries.PLAYER_BURNING_BOOST, 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;

        TemperatureConstants.PLAYER_POWDER_SNOW_BOOST = Maths.clampPositiveI(
                reader.getInt(ConfigEntries.PLAYER_POWDER_SNOW_BOOST, 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;
    }

    private void loadInterpolationFunction() {
        String interpolationFunctionName = reader.getString(ConfigEntries.INTERPOLATION_FUNCTION,
                TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        TemperatureConstants.INTERPOLATION_FUNCTION = TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }
}
