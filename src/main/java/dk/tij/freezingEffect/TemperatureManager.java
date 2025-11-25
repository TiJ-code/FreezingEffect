package dk.tij.freezingEffect;

import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemperatureManager {
    private final JavaPlugin plugin;
    private final PlayerDataHandler playerDataHandler;

    private final Map<Player, Double> playerTemps = new HashMap<>();

    private BukkitRunnable decayTask;

    public TemperatureManager(JavaPlugin plugin, PlayerDataHandler playerDataHandler) {
        this.plugin = plugin;
        this.playerDataHandler = playerDataHandler;
    }

    public void savePlayer(Player player) {
        double temperature = getTemperature(player);
        playerDataHandler.savePlayerTemperature(player, temperature);
        playerTemps.remove(player);
    }

    public void loadPlayer(Player player) {
        double temperature = playerDataHandler.loadPlayerTemperature(player, TemperatureConstants.DEFAULT_TEMPERATURE);
        playerTemps.put(player, temperature);
    }

    public void startDecayTask() {
        if (decayTask == null) {
            decayTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (isNearHeatSource(player)) continue;

                        double temp = getTemperature(player) - TemperatureConstants.TEMPERATURE_DECAY;
                        temp = Math.max(temp, 0);
                        setTemperature(player, temp);
                        player.sendMessage(String.format("Your temperature is %.2f°", temp));
                        applyFreezingEffects(player, temp);
                    }
                }
            };
        }
        decayTask.runTaskTimer(plugin, 0L, 20L * 5);
    }

    public void stopDecayTask() {
        if (decayTask == null)
            return;
        decayTask.cancel();
    }

    public void reloadDecayTask() {
        stopDecayTask();
        startDecayTask();
    }

    private void applyFreezingEffects(Player player, double temperature) {
        if (temperature < TemperatureConstants.FREEZING_THRESHOLD) {
            int level = (int) ((TemperatureConstants.FREEZING_THRESHOLD - temperature) / 5);
            level = Math.min(level, 4);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, level));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, level));

            if (temperature <= TemperatureConstants.CRITICAL_FREEZING_THRESHOLD) {
                player.damage(1.0);
                player.sendMessage("§cYou are freezing!");
            }
        }
    }

    private boolean isNearHeatSource(Player player) {
        int heatRadius = TemperatureConstants.HEAT_RADIUS;

        Location location = player.getLocation();

        for (int x = -heatRadius; x <= heatRadius; x++) {
            for (int y = -heatRadius; y <= heatRadius; y++) {
                for (int z = -heatRadius; z <= heatRadius; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    if (TemperatureConstants.HEAT_SOURCES.contains(block.getType())) return true;
                }
            }
        }

        return false;
    }

    public void setTemperature(Player player, double temperature) {
        playerTemps.put(player, temperature);
    }

    public double getTemperature(Player player) {
        return playerTemps.getOrDefault(player, TemperatureConstants.DEFAULT_TEMPERATURE);
    }
}
