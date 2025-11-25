package dk.tij.freezingEffect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemperatureManager {
    private final JavaPlugin plugin;

    private final Map<Player, Double> playerTemps = new HashMap<>();

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
        return playerTemps.getOrDefault(player, 0.0d);
    }
}
