package dk.tij.freezingEffect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemperatureManager {
    private final JavaPlugin plugin;

    private final Map<Player, Double> playerTemps = new HashMap<>();

    private final double defaultTemperature = 100.0,
                         temperatureDecay = 1.0;

    private final Set<Material> heatSources = Set.of(
            Material.TORCH,
            Material.CAMPFIRE,
            Material.FIRE,
            Material.LAVA,
            Material.LANTERN
    );

    public TemperatureManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setTemperature(Player player, double temperature) {
        playerTemps.put(player, temperature);
    }

    public double getTemperature(Player player) {
        return playerTemps.getOrDefault(player, defaultTemperature);
    }

    public void startDecayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    double temp = getTemperature(player) - temperatureDecay;
                    temp = Math.max(temp, 0);
                    setTemperature(player, temp);
                    player.sendMessage(String.format("Your temperature is %.2f°", temp));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 5);
    }
}
