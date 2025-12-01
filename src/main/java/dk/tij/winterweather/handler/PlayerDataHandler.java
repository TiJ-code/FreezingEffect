package dk.tij.winterweather.handler;

import dk.tij.winterweather.constants.PlayerConfigEntries;
import dk.tij.winterweather.utils.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PlayerDataHandler {
    private static PlayerDataHandler instance;

    private final File configFile;
    private FileConfiguration config;

    public PlayerDataHandler(JavaPlugin plugin) {
        if (instance != null)
            throw new RuntimeException("Only one allowed at runtime");
        instance = this;
        this.configFile = new File(plugin.getDataFolder(), "playerdata.yml");
        FileUtils.createFileIfNotExistent(configFile);
        loadConfig();
    }

    public void savePlayerFreezeTicks(Player player, int actualFreezeTicks) {
        config.set(getPlayerFreezePointsConfigEntry(player.getUniqueId().toString()), actualFreezeTicks);
        saveConfig();
    }

    public int loadPlayerFreezeTicks(Player player) {
        return config.getInt(getPlayerFreezePointsConfigEntry(player.getUniqueId().toString()), 0);
    }

    public void savePlayerShowDebug(Player player, boolean showDebug) {
        config.set(getPlayerShowDebugConfigEntry(player.getUniqueId().toString()), showDebug);
        saveConfig();
    }

    public boolean loadPlayerShowDebug(Player player) {
        return config.getBoolean(getPlayerShowDebugConfigEntry(player.getUniqueId().toString()), false);
    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException ignored) {}
    }

    private static String getPlayerFreezePointsConfigEntry(String uuid) {
        return PlayerConfigEntries.CATEGORY_PLAYERS_P + uuid + PlayerConfigEntries.PLAYER_FREEZE_TICKS;
    }

    private static String getPlayerShowDebugConfigEntry(String uuid) {
        return PlayerConfigEntries.CATEGORY_PLAYERS_P + uuid + PlayerConfigEntries.PLAYER_SHOW_DEBUG;
    }
}
