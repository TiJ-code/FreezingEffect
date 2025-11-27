package dk.tij.freezingEffect;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.handler.FreezeHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TemperatureManager {
    private final JavaPlugin plugin;
    private final FreezeHandler freezeHandler;
    private final DamageSource freezingDamageSource;

    private final Map<Player, Double> playerFreezeAcc = new HashMap<>();

    private BukkitRunnable decayTask;

    public TemperatureManager(JavaPlugin plugin, FreezeHandler freezeHandler) {
        this.plugin = plugin;
        this.freezeHandler = freezeHandler;
        this.freezingDamageSource = DamageSource.builder(DamageType.FREEZE).build();
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
        decayTask.runTaskTimer(plugin, 0L, 20L);
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
        double current = playerFreezeAcc.getOrDefault(player, 0.0);
        double target = computeTarget(player); // 0 or MAX_FREEZE_TICKS

        int freezeTicks = 0;
        if (current != target) {
            int direction = current < target ? 1 : -1;

            double t = current / TemperatureConstants.VANILLA_MAX_FREEZE_TICKS;
            double curve = InterpolationFunctions.smootherstep(t);

            double delta = TemperatureConstants.TEMPERATURE_DECAY * (1.0 - curve);

            current += delta * direction;

            current = Math.max(0, Math.min(current, TemperatureConstants.VANILLA_MAX_FREEZE_TICKS));

            playerFreezeAcc.put(player, current);
            
            freezeTicks = (int) Math.round(current);
            player.setFreezeTicks(freezeTicks);
            freezeHandler.updatePlayer(player, freezeTicks);

            applyDamage(player, freezeTicks);
        }

        player.sendMessage(String.format("FT: %3d | T: %5.2f",  freezeTicks, target));
    }

    private void applyDamage(Player player, int freeze) {
        if (freeze >= TemperatureConstants.VANILLA_MAX_FREEZE_TICKS) {
            player.damage(1, freezingDamageSource);
        }
    }

    private int computeTarget(Player player) {
        return isNearHeatSource(player) ? 0 : TemperatureConstants.VANILLA_MAX_FREEZE_TICKS;
    }

    private int computeDelta(Player player, int current, int target) {
        if (current == target) return 0;

        // direction is always +1 unless cooling near heat
        int direction = Integer.compare(target, current);

        double t = current / (double) TemperatureConstants.VANILLA_MAX_FREEZE_TICKS;

        // smootherstep gives slow → fast → slow
        double curve = InterpolationFunctions.smootherstep(t);

        // the magic: slow at edges, fastest in middle
        double speed = TemperatureConstants.TEMPERATURE_DECAY;

        // we use (1 - curve) when heating up
        double raw = speed * (1.0 - curve);

        // floor allows repeated numbers
        int step = (int) Math.floor(raw);

        // always move at least 1 when going upward
        if (step == 0 && direction > 0) step = 1;

        return step * direction;
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

    public void setTemperature(Player player, int temperature) {
        player.setFreezeTicks(temperature);
    }

    public int getTemperature(Player player) {
        return player.getFreezeTicks();
    }
}
