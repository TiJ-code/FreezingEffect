package dk.tij.winterweather.config;

import dk.tij.winterweather.WinterWeather;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReader {
    private final WinterWeather plugin;
    private FileConfiguration config;

    public ConfigReader(WinterWeather plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        this.config = plugin.getConfig();
    }

    public String getString(String path, String defaultValue) {
        if (!config.contains(path)) {
            plugin.getLogger().warning("Missing config path: " + path);
            return defaultValue;
        }
        return config.getString(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        if (!config.contains(path)) {
            plugin.getLogger().warning("Missing config path: " + path + ", using default: " + defaultValue);
            return defaultValue;
        }
        return config.getInt(path, defaultValue);
    }

    public double getDouble(String path, double defaultValue) {
        if (!config.contains(path)) {
            plugin.getLogger().warning("Missing config path: " + path + ", using default: " + defaultValue);
            return defaultValue;
        }
        return config.getDouble(path, defaultValue);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        if (!config.contains(path)) {
            plugin.getLogger().warning("Missing config path: " + path + ", using default: " + defaultValue);
            return defaultValue;
        }
        return config.getBoolean(path, defaultValue);
    }
}
