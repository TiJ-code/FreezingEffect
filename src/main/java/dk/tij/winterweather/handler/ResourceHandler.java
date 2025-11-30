package dk.tij.winterweather.handler;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.ConfigEntries;
import dk.tij.winterweather.config.ConfigReader;
import dk.tij.winterweather.constants.TimeConstants;
import dk.tij.winterweather.utils.HeatSource;
import dk.tij.winterweather.constants.TemperatureConstants;
import dk.tij.winterweather.utils.InterpolationFunctions;
import dk.tij.winterweather.utils.ItemUtils;
import dk.tij.winterweather.utils.Maths;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

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
        loadConfig();
    }

    public void setEnabled(boolean enabled) {
        plugin.getConfig().set(ConfigEntries.ENABLED, enabled);
        plugin.saveConfig();
    }

    public boolean isEnabled() {
        return reader.getBoolean(ConfigEntries.ENABLED, false);
    }

    public void setCustomDayCycleEnabled(boolean enabled) {
        plugin.getConfig().set(ConfigEntries.DAYLIGHT_CUSTOM_CYCLE_ENABLE, enabled);
        TimeConstants.CUSTOM_DAY_CYCLE_ENABLE = enabled;
        plugin.enableCustomDayCycle(enabled);
        plugin.saveConfig();
    }

    private void loadConfig() {
        loadCustomDayCycleValues();
        loadFrostPlayerStats();
        loadIsolationValues();
        loadHeatSourceValues();
    }

    private void loadCustomDayCycleValues() {
        TimeConstants.CUSTOM_DAY_CYCLE_ENABLE = reader.getBoolean(ConfigEntries.DAYLIGHT_CUSTOM_CYCLE_ENABLE, true);
        TimeConstants.DAY_CYCLE_LENGTH_MINUTES = Maths.clampPositiveI(
                reader.getInt(ConfigEntries.DAYLIGHT_TOTAL_CYCLE_MINUTES, TimeConstants.VANILLA_TOTAL_CYCLE_MINUTES)
        );
        TimeConstants.DAY_PERCENTAGE = Maths.clampI0To100(
                reader.getInt(ConfigEntries.DAYLIGHT_DAY_PERCENTAGE, TimeConstants.VANILLA_DAY_PERCENTAGE)
        ) * TO_PERCENT_CONVERSION_FACTOR;

        TimeConstants.INTERPOLATION_FUNCTION = loadInterpolationFunction(ConfigEntries.DAYLIGHT_INTERPOLATION_FUNCTION);

        TimeConstants.T_SUNRISE_START = Maths.clampI(
                reader.getInt(ConfigEntries.DAYLIGHT_T_SUNRISE_START, TimeConstants.VANILLA_T_SUNRISE_START),
                0, TimeConstants.VANILLA_TICKS_PER_DAY
        );
        TimeConstants.T_SUNRISE_END = Maths.clampI(
                reader.getInt(ConfigEntries.DAYLIGHT_T_SUNRISE_END, TimeConstants.VANILLA_T_SUNRISE_END),
                0, TimeConstants.VANILLA_TICKS_PER_DAY
        );
        TimeConstants.T_SUNDOWN_START = Maths.clampI(
                reader.getInt(ConfigEntries.DAYLIGHT_T_SUNDOWN_START, TimeConstants.VANILLA_T_SUNDOWN_START),
                0, TimeConstants.VANILLA_TICKS_PER_DAY
        );
        TimeConstants.T_SUNDOWN_END = Maths.clampI(
                reader.getInt(ConfigEntries.DAYLIGHT_T_SUNDOWN_END, TimeConstants.VANILLA_T_SUNDOWN_END),
                0, TimeConstants.VANILLA_TICKS_PER_DAY
        );
        TimeConstants.T_SUNRISE_DURATION = Maths.clampPositiveI(TimeConstants.T_SUNRISE_END - TimeConstants.T_SUNRISE_START);
        TimeConstants.T_SUNDOWN_DURATION = Maths.clampPositiveI(TimeConstants.T_SUNDOWN_END - TimeConstants.T_SUNDOWN_START);

        double dayDuration = TimeConstants.DAY_CYCLE_LENGTH_MINUTES * TimeConstants.DAY_PERCENTAGE;
        double nightDuration = TimeConstants.DAY_CYCLE_LENGTH_MINUTES - dayDuration;

        double dayLengthGameTicks = dayDuration * TimeConstants.MATH_MINUTES_TO_TICKS_FACTOR;
        double nightLengthGameTicks = nightDuration * TimeConstants.MATH_MINUTES_TO_TICKS_FACTOR;

        TimeConstants.DAY_INCREMENT_PER_TICK = TimeConstants.VANILLA_TICKS_PER_HALF_DAY / dayLengthGameTicks;
        TimeConstants.NIGHT_INCREMENT_PER_TICK = TimeConstants.VANILLA_TICKS_PER_HALF_DAY / nightLengthGameTicks;
    }

    private void loadFrostPlayerStats() {
        TemperatureConstants.CRITICAL_FREEZING_TICKS = Maths.clampPositiveI(
                reader.getInt(ConfigEntries.FROST_CRITICAL_FREEZING_TICKS, Integer.MAX_VALUE)
        );
        TemperatureConstants.PLAYER_RADIUS = Maths.clampPositiveIntD(
                reader.getDouble(ConfigEntries.FROST_PLAYER_RADIUS, 0)
        );
        TemperatureConstants.PLAYER_RADIUS_SQUARED = TemperatureConstants.PLAYER_RADIUS * TemperatureConstants.PLAYER_RADIUS;
        TemperatureConstants.INTERPOLATION_FUNCTION = loadInterpolationFunction(ConfigEntries.FROST_INTERPOLATION_FUNCTION);
    }

    private void loadIsolationValues() {
        ConfigurationSection isolationSection = plugin.getConfig().getConfigurationSection(ConfigEntries.FROST_SUB_CATEGORY_ISOLATION);

        if (isolationSection == null) return;

        int maxPossibleIsolationValue = Maths.clampI0To100(isolationSection.getInt(ConfigEntries.FROST_ISOLATION_MAX_POSSIBLE_ISOLATION, 0));
        TemperatureConstants.MAX_POSSIBLE_ISOLATION = maxPossibleIsolationValue * TO_PERCENT_CONVERSION_FACTOR;

        ConfigurationSection armourSection = isolationSection.getConfigurationSection(ConfigEntries.FROST_ISOLATION_ARMOUR_PIECES);

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
        ConfigurationSection heatSourceSection = plugin.getConfig().getConfigurationSection(ConfigEntries.FROST_SUB_CATEGORY_HEAT_SOURCES);

        if (heatSourceSection == null) return;

        for (String key : heatSourceSection.getKeys(false)) {
            Material material = Material.matchMaterial(key);

            if (material == null) continue;

            double value = Maths.clampPositiveIntD(heatSourceSection.getDouble(key + ConfigEntries.FROST_HEAT_SOURCE_VALUE));
            double radius = Maths.clampPositiveIntD(heatSourceSection.getInt(key + ConfigEntries.FROST_HEAT_SOURCE_RADIUS));
            TemperatureConstants.HEAT_SOURCE_WARMING.put(material, new HeatSource(value, radius*radius));
        }

        TemperatureConstants.PLAYER_BURNING_BOOST = Maths.clampI0To100(
                reader.getInt(ConfigEntries.FROST_PLAYER_BURNING_BOOST, 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;

        TemperatureConstants.PLAYER_POWDER_SNOW_BOOST = Maths.clampPositiveI(
                reader.getInt(ConfigEntries.FROST_PLAYER_POWDER_SNOW_BOOST, 0)
        ) * TO_PERCENT_CONVERSION_FACTOR + 1d;
    }

    private Function<Double, Double> loadInterpolationFunction(String configKey) {
        String interpolationFunctionName = reader.getString(configKey,
                InterpolationFunctions.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        return InterpolationFunctions.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }
}
