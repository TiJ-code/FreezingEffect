package dk.tij.freezingEffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    private final int heatRadius = 5;

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

    public void startDecayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isNearHeatSource(player)) continue;

                    double temp = getTemperature(player) - temperatureDecay;
                    temp = Math.max(temp, 0);
                    setTemperature(player, temp);
                    player.sendMessage(String.format("Your temperature is %.2f°", temp));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 5);
    }

    private boolean isNearHeatSource(Player player) {
        Location location = player.getLocation();

        for (int x = -heatRadius; x <= heatRadius; x++) {
            for (int y = -heatRadius; y <= heatRadius; y++) {
                for (int z = -heatRadius; z <= heatRadius; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (heatSources.contains(block.getType())) return true;
                }
            }
        }

        return false;
    }

    public void setTemperature(Player player, double temperature) {
        playerTemps.put(player, temperature);
    }

    public double getTemperature(Player player) {
        return playerTemps.getOrDefault(player, defaultTemperature);
    }

    public double getDefaultTemperature() {
        return defaultTemperature;
    }
}
