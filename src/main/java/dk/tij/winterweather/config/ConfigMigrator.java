package dk.tij.winterweather.config;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.ConfigEntries;
import org.bukkit.configuration.file.FileConfiguration;

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
            renameEntry(cfg, oldPath, ConfigEntries.PLAYER_RADIUS);
        });

        migrations.put(2, cfg -> {
            String path = ConfigEntries.SUB_CATEGORY_ISOLATION + "." + ConfigEntries.ISOLATION_ARMOUR_PIECES;
            if (cfg.contains(path)) {
                Objects.requireNonNull(cfg.getConfigurationSection(path)).getKeys(false).forEach(key -> {
                    Object value = config.get(path + "." + key);
                    if (value instanceof Integer) {
                        cfg.set(path + "." + key, Map.of(ConfigEntries.ISOLATION_ARMOUR_PIECE_VALUE, value));
                        plugin.getLogger().info("Migrated " + path + "." + key + " » map with value");
                    }
                });
            }
        });

        migrations.put(4, cfg -> removeEntry(config, "debug"));
    }

    public void migrate() {
        int currentVersion = config.getInt(ConfigEntries.CONFIG_VERSION_ENTRY, 0);
        int targetVersion = migrations.keySet().stream().mapToInt(v -> v).max().orElse(currentVersion);

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
