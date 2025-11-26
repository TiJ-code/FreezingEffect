package dk.tij.freezingEffect;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.handler.FreezeHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TemperatureManager {
    private final Map<Player, Double> playerTemps = new HashMap<>();
    private final JavaPlugin plugin;
    private final PlayerDataHandler playerDataHandler;
    private final FreezeHandler freezeHandler;

    private BukkitRunnable decayTask;

    public TemperatureManager(JavaPlugin plugin, PlayerDataHandler playerDataHandler, FreezeHandler freezeHandler) {
        this.plugin = plugin;
        this.playerDataHandler = playerDataHandler;
        this.freezeHandler = freezeHandler;
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
                        tickPlayerTemperature(player);
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

    private void tickPlayerTemperature(Player player) {
        final double temperatureRange = TemperatureConstants.DEFAULT_TEMPERATURE - TemperatureConstants.FREEZING_THRESHOLD;

        double temperature = getTemperature(player);

        double t = (temperature - TemperatureConstants.FREEZING_THRESHOLD) / temperatureRange;
        t = InterpolationFunctions.clamp(t, 0d, 1d);

        if (!isNearHeatSource(player)) {
            double decayFactor = InterpolationFunctions.smoothstep(1 - t);
            temperature -= decayFactor * TemperatureConstants.TEMPERATURE_DECAY;
            temperature = Math.max(0, temperature);
            setTemperature(player, temperature);
        }

        double overlayPercent = computeOverlayPercentage(temperature);
        int freezeTicks = (int) (overlayPercent * player.getMaxFreezeTicks());

        freezeHandler.updatePlayer(player, freezeTicks);
        player.setFreezeTicks(freezeTicks);

        player.sendMessage(String.format("Your temperature is %.2f° | Overlay: %5.2f%% | FreezeTicks: %3d", temperature, overlayPercent * 100, player.getFreezeTicks()));
    }

    private double computeOverlayPercentage(double temperature) {
        if (temperature <= TemperatureConstants.CRITICAL_FREEZING_THRESHOLD) {
            return 1.0;
        } else {
            double overlayT = (temperature - TemperatureConstants.CRITICAL_FREEZING_THRESHOLD) /
                    (TemperatureConstants.FREEZING_THRESHOLD - TemperatureConstants.CRITICAL_FREEZING_THRESHOLD);
            overlayT = InterpolationFunctions.clamp(overlayT, 0d, 1d);

            return 1.0 - InterpolationFunctions.smootherstep(overlayT);
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
