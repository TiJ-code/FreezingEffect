package dk.tij.freezingEffect.config;

import dk.tij.freezingEffect.FreezingEffect;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigMigrator {
    private final FreezingEffect plugin;
    private final FileConfiguration config;
    private final Map<Integer, Consumer<FileConfiguration>> migrations = new HashMap<>();

    public ConfigMigrator(FreezingEffect plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        setupMigrations();
    }

    private void setupMigrations() {
        migrations.put(1, cfg -> {
            String oldPath = ConfigEntries.CATEGORY_FROST + ".heatRadius";
            renameEntry(cfg, oldPath, ConfigEntries.PLAYER_RADIUS);
        });
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
}
