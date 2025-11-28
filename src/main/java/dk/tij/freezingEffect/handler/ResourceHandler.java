package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
        TemperatureConstants.CRITICAL_FREEZING_TICKS = config.getInt("frost.criticalFreezingTicks", Integer.MAX_VALUE);
        TemperatureConstants.HEAT_RADIUS = config.getInt("frost.heatRadius", 0);

        loadIsolationValues();

        Set<Material> configHeatSources = config.getStringList("frost.heatSources")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
        TemperatureConstants.HEAT_SOURCES.addAll(configHeatSources);

        String interpolationFunctionName = config.getString("frost.interpolationFunction",
                TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]);
        TemperatureConstants.INTERPOLATION_FUNCTION = TemperatureConstants.INTERPOLATION_FUNCTIONS_MAPPING.get(interpolationFunctionName);
    }

    private void loadIsolationValues() {
        ConfigurationSection isolationSection = config.getConfigurationSection("frost.isolation");

        if (isolationSection == null) return;

        int maxPossibleIsolationValue = InterpolationFunctions.clampI(
                isolationSection.getInt("maxPossibleIsolation", 0),
                0, 100
        );
        TemperatureConstants.MAX_POSSIBLE_ISOLATION = maxPossibleIsolationValue * TO_PERCENT_CONVERSION_FACTOR;

        ConfigurationSection armourSection = isolationSection.getConfigurationSection("armourPieces");

        if (armourSection == null) return;

        for (String key : armourSection.getKeys(false)) {
            Material material = Material.getMaterial(key);

            if (!ItemUtils.isArmourItem(material)) continue;

            int value = InterpolationFunctions.clampI(
                    armourSection.getInt(key),
                    0, 100
            );
            TemperatureConstants.ARMOUR_PIECE_ISOLATION.put(material,
                    value * TO_PERCENT_CONVERSION_FACTOR);
        }
    }
}
