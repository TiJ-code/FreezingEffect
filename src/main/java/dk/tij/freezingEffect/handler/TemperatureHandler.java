package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TemperatureHandler {
    private final JavaPlugin plugin;
    private final FreezeHandler freezeHandler;

    private final Map<UUID, Integer> actualPlayerFreezeTicks = new HashMap<>();

    private BukkitRunnable decayTask;

    public TemperatureHandler(JavaPlugin plugin, FreezeHandler freezeHandler) {
        this.plugin = plugin;
        this.freezeHandler = freezeHandler;
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
        decayTask.runTaskTimer(plugin, 0L, 1L);
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
        int target = computeTarget(player);

        UUID playerUUID = player.getUniqueId();
        int nextActualFreezeTick = actualPlayerFreezeTicks.get(playerUUID) + target;
        nextActualFreezeTick = InterpolationFunctions.clampInt(nextActualFreezeTick, 0, Integer.MAX_VALUE);
        actualPlayerFreezeTicks.put(playerUUID, nextActualFreezeTick);

        int freezeTicks = updatePlayerFreezingPoints(player, nextActualFreezeTick);

        player.sendMessage(String.format("FreezeTicks %3d | Target: %1d | FreezePoints: %4d",  freezeTicks, target, nextActualFreezeTick));
    }

    private int computeTarget(Player player) {
        return isNearHeatSource(player) ? -1 : 1;
    }

    private boolean isNearHeatSource(Player player) {
        int heatRadius = TemperatureConstants.HEAT_RADIUS;
        Location location = player.getLocation();

        final int blockX = location.getBlockX(),
                  blockY = location.getBlockY(),
                  blockZ = location.getBlockZ();

        final int maxX = blockX + heatRadius,
                  maxY = blockY + heatRadius,
                  maxZ = blockZ + heatRadius;
        final int minX = blockX - heatRadius,
                  minY = blockY - heatRadius,
                  minZ = blockZ - heatRadius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    Material type = block.getType();
                    if (TemperatureConstants.HEAT_SOURCES.contains(type)) return true;
                }
            }
        }

        return false;
    }

    private int updatePlayerFreezingPoints(Player player, int actualFreezeTicks) {
        double interpolatedFreezingPoints = TemperatureConstants.INTERPOLATION_FUNCTION
                        .apply( (double) actualFreezeTicks / TemperatureConstants.CRITICAL_FREEZING_TICKS );
        double scaledInterpolatedFreezingPoints = interpolatedFreezingPoints * TemperatureConstants.VANILLA_MAX_FREEZE_TICKS;
        int freezeTicks = Math.max( (int) (scaledInterpolatedFreezingPoints + 0.5d), TemperatureConstants.VANILLA_MIN_FREEZE_TICKS );

        player.setFreezeTicks(freezeTicks);
        freezeHandler.updatePlayer(player, freezeTicks);

        return freezeTicks;
    }

    public void registerPlayer(Player player, int actualFreezeTicks) {
        actualPlayerFreezeTicks.put(player.getUniqueId(), actualFreezeTicks);
        updatePlayerFreezingPoints(player, actualFreezeTicks);
    }

    public void resetPlayer(Player player) {
        registerPlayer(player, 0);
        updatePlayerFreezingPoints(player, 0);
    }

    public int getPlayerFreezePoints(Player player) {
        return actualPlayerFreezeTicks.get(player.getUniqueId());
    }
}
