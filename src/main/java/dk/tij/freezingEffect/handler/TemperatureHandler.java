package dk.tij.freezingEffect.handler;

import dk.tij.freezingEffect.constants.InterpolationFunctions;
import dk.tij.freezingEffect.constants.TemperatureConstants;
import dk.tij.freezingEffect.toolbox.Pair;
import dk.tij.freezingEffect.utils.ItemUtils;
import dk.tij.freezingEffect.utils.TemperatureUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TemperatureHandler {
    private final JavaPlugin plugin;
    private final FreezeHandler freezeHandler;

    private final Map<UUID, Double> actualPlayerFreezeTicks = new HashMap<>();
    private final DamageSource freezingDamageSource;

    private BukkitRunnable decayTask;
    private BukkitRunnable leatherArmourDamageTask;

    public TemperatureHandler(JavaPlugin plugin, FreezeHandler freezeHandler) {
        this.plugin = plugin;
        this.freezeHandler = freezeHandler;
        this.freezingDamageSource = DamageSource.builder(DamageType.FREEZE).build();
    }

    public void startDecayTask() {
        if (decayTask == null) {
            decayTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> tickPlayerTemperature(player));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        tickPlayerTemperature(player);
                    }
                }
            };
        }
        if (leatherArmourDamageTask == null) {
            leatherArmourDamageTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> applyDamageIfLeatherArmour(player));
                }
            };
        }
        decayTask.runTaskTimer(plugin, 0L, 1L);
        leatherArmourDamageTask.runTaskTimer(plugin, 1L, TemperatureConstants.VANILLA_DAMAGE_FREEZE_TICKS);
    }

    public void stopDecayTask() {
        if (decayTask == null) return;
        if (leatherArmourDamageTask == null) return;
        decayTask.cancel();
        leatherArmourDamageTask.cancel();
    }

    public void reloadDecayTask() {
        stopDecayTask();
        startDecayTask();
    }

    private void tickPlayerTemperature(Player player) {
        double target = computeTarget(player);

        double armourReduction = TemperatureUtils.getLeatherReduction(player);

        double fractionalChange = target > 0
                ? target * (1.0 - armourReduction)
                : target;

        if (TemperatureUtils.isPlayerBurning(player)) {
            double fireValue = -TemperatureConstants.HEAT_SOURCE_WARMING
                    .getOrDefault(Material.FIRE, Pair.of(1d, TemperatureConstants.HEAT_RADIUS)).getK();
            fireValue *= TemperatureConstants.PLAYER_BURNING_BOOST;
            if (fractionalChange < 0)
                fractionalChange += fireValue;
            else
                fractionalChange = fireValue;
        }

        UUID playerUUID = player.getUniqueId();

        double previousActualFreezeTicks = actualPlayerFreezeTicks.get(playerUUID);
        double nextActualFreezeTick = previousActualFreezeTicks + fractionalChange;
        nextActualFreezeTick = InterpolationFunctions.clampD(nextActualFreezeTick, 0, Integer.MAX_VALUE);
        actualPlayerFreezeTicks.put(playerUUID, nextActualFreezeTick);

        int freezeTicks = updatePlayerFreezingPoints(player, (int) nextActualFreezeTick);

        player.sendMessage(String.format("FreezeTicks %3d | FractionalTarget: %3.1f | FreezePoints: %4.2f",
                freezeTicks, fractionalChange, nextActualFreezeTick));
    }

    private void applyDamageIfLeatherArmour(Player player) {
        if (!ItemUtils.isWearingLeatherArmour(player)) return;
        if (player.getFreezeTicks() <= TemperatureConstants.VANILLA_MAX_FREEZE_TICKS * 1.1) return;

        player.damage(1d, freezingDamageSource);
    }

    private double computeTarget(Player player) {
        return -TemperatureUtils.getNumberOfHeatSourcesNearby(player);
    }

    private int updatePlayerFreezingPoints(Player player, int actualFreezeTicks) {
        double interpolatedFreezingPoints = TemperatureConstants.INTERPOLATION_FUNCTION.apply(
                (double) actualFreezeTicks / TemperatureConstants.CRITICAL_FREEZING_TICKS
        );
        double scaledInterpolatedFreezingPoints = interpolatedFreezingPoints * TemperatureConstants.VANILLA_MAX_FREEZE_TICKS;
        int freezeTicks = Math.max( (int) (scaledInterpolatedFreezingPoints + 0.5d), TemperatureConstants.VANILLA_MIN_FREEZE_TICKS );

        player.setFreezeTicks(freezeTicks);
        freezeHandler.updatePlayer(player, freezeTicks);

        return freezeTicks;
    }

    public void registerPlayer(Player player, double actualFreezeTicks) {
        actualPlayerFreezeTicks.put(player.getUniqueId(), actualFreezeTicks);
        updatePlayerFreezingPoints(player, (int) actualFreezeTicks);
    }

    public void resetPlayer(Player player) {
        registerPlayer(player, 0);
        updatePlayerFreezingPoints(player, 0);
    }

    public double getActualPlayerFreezeTicks(Player player) {
        return actualPlayerFreezeTicks.get(player.getUniqueId());
    }
}
