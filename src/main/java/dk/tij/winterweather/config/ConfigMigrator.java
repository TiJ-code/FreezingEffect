package dk.tij.winterweather.config;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.ConfigEntries;
import dk.tij.winterweather.constants.TimeConstants;
import dk.tij.winterweather.utils.InterpolationFunctions;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigMigrator {
    private final WinterWeather plugin;
    private final FileConfiguration config;
    private final Map<Integer, Consumer<FileConfiguration>> migrations = new HashMap<>();

    public ConfigMigrator(WinterWeather plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupMigrations();
    }

    private void setupMigrations() {
        migrations.put(1, cfg -> {
            String oldPath = ConfigEntries.CATEGORY_FROST + ".heatRadius";
            renameEntry(cfg, oldPath, ConfigEntries.FROST_PLAYER_RADIUS);
        });

        migrations.put(2, cfg -> {
            String path = ConfigEntries.FROST_SUB_CATEGORY_ISOLATION + "." + ConfigEntries.FROST_ISOLATION_ARMOUR_PIECES;
            if (cfg.contains(path)) {
                Objects.requireNonNull(cfg.getConfigurationSection(path)).getKeys(false).forEach(key -> {
                    Object value = cfg.get(path + "." + key);
                    if (value instanceof Integer) {
                        cfg.set(path + "." + key, Map.of(ConfigEntries.FROST_ISOLATION_ARMOUR_PIECE_VALUE, value));
                        plugin.getLogger().info("Migrated " + path + "." + key + " » map with value");
                    }
                });
            }
        });

        migrations.put(3, cfg -> {
            addEntry(cfg, ConfigEntries.ENABLED, false);
        });

        migrations.put(4, cfg -> removeEntry(cfg, "debug"));

        migrations.put(5, cfg -> {
            addEntry(cfg, ConfigEntries.DAYLIGHT_CUSTOM_CYCLE_ENABLE, TimeConstants.CUSTOM_DAY_CYCLE_ENABLE);
            addEntry(cfg, ConfigEntries.DAYLIGHT_TOTAL_CYCLE_MINUTES, TimeConstants.VANILLA_TOTAL_CYCLE_MINUTES);
            addEntry(cfg, ConfigEntries.DAYLIGHT_DAY_PERCENTAGE, TimeConstants.VANILLA_DAY_PERCENTAGE);
        });

        migrations.put(6, cfg -> addEntry(cfg, ConfigEntries.DAYLIGHT_INTERPOLATION_FUNCTION, InterpolationFunctions.INTERPOLATION_FUNCTIONS_MAPPING.keySet().toArray(String[]::new)[0]));

        migrations.put(7, cfg -> {
            addEntry(cfg, ConfigEntries.DAYLIGHT_T_SUNRISE_START, TimeConstants.VANILLA_T_SUNRISE_START);
            addEntry(cfg, ConfigEntries.DAYLIGHT_T_SUNRISE_END, TimeConstants.VANILLA_T_SUNRISE_END);
            addEntry(cfg, ConfigEntries.DAYLIGHT_T_SUNDOWN_START, TimeConstants.VANILLA_T_SUNDOWN_START);
            addEntry(cfg, ConfigEntries.DAYLIGHT_T_SUNDOWN_END, TimeConstants.VANILLA_T_SUNDOWN_END);
        });
    }

    public void migrate() {
        int currentVersion = config.getInt(ConfigEntries.CONFIG_VERSION_ENTRY, 0);
        int targetVersion = ConfigEntries.CURRENT_VERSION;

        boolean changed = false;
        for (int version = currentVersion + 1; version <= targetVersion; version++) {
            Consumer<FileConfiguration> migration = migrations.get(version);
            if (migration != null) {
                migration.accept(config);
                changed = true;
                plugin.getLogger().info("Applied migration for config version " + version);
            }
        }

        if (changed) {
            config.set(ConfigEntries.CONFIG_VERSION_ENTRY, targetVersion);
            plugin.saveConfig();
            plugin.getLogger().info("Updated " + ConfigEntries.CONFIG_VERSION_ENTRY + " to " + targetVersion);
        }
    }

    private <T> void addEntry(FileConfiguration cfg, String path, T t) {
        cfg.set(path, t);
        plugin.getLogger().info("Added config entry " + path + " = " + t);
    }

    private void renameEntry(FileConfiguration cfg, String oldPath, String newPath) {
        if (cfg.contains(oldPath) && !cfg.contains(newPath)) {
            Object value = cfg.get(oldPath);
            cfg.set(newPath, value);
            cfg.set(oldPath, null);
            plugin.getLogger().info("Migrated config key: " + oldPath + " to " + newPath);
        }
    }

    private void removeEntry(FileConfiguration cfg, String path) {
        if (cfg.contains(path)) {
            cfg.set(path, null);
            plugin.getLogger().info("Migrated config key: " + path + " removed");
        }
    }
}
