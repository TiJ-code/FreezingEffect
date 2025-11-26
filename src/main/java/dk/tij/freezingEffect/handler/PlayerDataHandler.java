package dk.tij.freezingEffect.handler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class PlayerDataHandler {
    private final File file;
    private final FileConfiguration config;

    public PlayerDataHandler(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public double loadPlayerTemperature(Player player, double defaultTemperature) {
        return config.getDouble("players." + player.getUniqueId() + ".temperature", defaultTemperature);
    }

    public void savePlayerTemperature(Player player, double temperature) {
        config.set("players." + player.getUniqueId() + ".temperature", temperature);
        save();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
