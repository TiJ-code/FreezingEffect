package dk.tij.winterweather.handler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PlayerDataHandler {
    private final File configFile;
    private FileConfiguration config;

    public PlayerDataHandler(JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder(), "playerdata.yml");
        createFileIfNotExistent();
        loadConfig();
    }

    public void savePlayerData(Player player, int actualFreezeTicks) {
        config.set(getPlayerFreezePointsConfigEntry(player.getUniqueId().toString()), actualFreezeTicks);
        saveConfig();
    }

    public int loadPlayerData(Player player) {
        return config.getInt(getPlayerFreezePointsConfigEntry(player.getUniqueId().toString()), 0);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {}
    }

    private void createFileIfNotExistent() {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
        } catch (IOException ignored) {}
    }

    private static String getPlayerFreezePointsConfigEntry(String uuid) {
        return "players." + uuid + ".actual_freeze_ticks";
    }
}
