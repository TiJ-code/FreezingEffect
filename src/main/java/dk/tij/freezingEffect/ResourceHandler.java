package dk.tij.freezingEffect;

import dk.tij.freezingEffect.constants.TemperatureConstants;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.stream.Collectors;

public class ResourceHandler {
    private final FileConfiguration config;

    public ResourceHandler(JavaPlugin plugin) {
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        TemperatureConstants.DEFAULT_TEMPERATURE = config.getDouble("frost.defaultTemperature", 0);
        TemperatureConstants.FREEZING_THRESHOLD = config.getDouble("frost.freezingThreshold", 0);
        TemperatureConstants.CRITICAL_FREEZING_THRESHOLD = config.getDouble("frost.criticalFreezingThreshold", 0);
        TemperatureConstants.TEMPERATURE_DECAY = config.getDouble("frost.temperatureDecay", 0);
        TemperatureConstants.HEAT_RADIUS = config.getInt("frost.heatRadius", 0);

        Set<Material> configHeatSources = config.getStringList("frost.heatSources")
                .stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
        TemperatureConstants.HEAT_SOURCES.addAll(configHeatSources);
    }
}
