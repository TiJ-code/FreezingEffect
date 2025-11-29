package dk.tij.freezingEffect.config;

import dk.tij.freezingEffect.FreezingEffect;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class ConfigMigrator {
    private final FreezingEffect plugin;
    private final FileConfiguration config;

    public ConfigMigrator(FreezingEffect plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void migrate(Map<String, String> migrations) {
        boolean changed = false;

        for (Map.Entry<String, String> entry : migrations.entrySet()) {
            String oldPath = entry.getKey();
            String newPath = entry.getValue();

            if (config.contains(oldPath) && !config.contains(newPath)) {
                config.set(newPath, config.get(oldPath));
                config.set(oldPath, null);
                plugin.getLogger().info("Migrated config key: " + oldPath + " » " + newPath);
                changed = true;
            }
        }
        if (changed) plugin.saveConfig();
    }
}
