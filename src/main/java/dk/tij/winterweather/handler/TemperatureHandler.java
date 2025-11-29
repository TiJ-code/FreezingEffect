package dk.tij.winterweather.handler;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.commands.utils.AdminDebug;
import dk.tij.winterweather.utils.*;
import dk.tij.winterweather.constants.TemperatureConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TemperatureHandler {
    private final WinterWeather plugin;
    private final FreezeHandler freezeHandler;

    private final Map<UUID, Double> actualPlayerFreezeTicks = new HashMap<>();
    private final DamageSource freezingDamageSource;

    private BukkitRunnable decayTask;
    private BukkitRunnable leatherArmourDamageTask;

    public TemperatureHandler(WinterWeather plugin, FreezeHandler freezeHandler) {
        this.plugin = plugin;
        this.freezeHandler = freezeHandler;
        this.freezingDamageSource = DamageSource.builder(DamageType.FREEZE).build();
    }

    public void startDecayTask() {
        decayTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getIsEnabled()) stopDecayTask();

                Bukkit.getOnlinePlayers().forEach(player -> tickPlayerTemperature(player));
            }
        };
        decayTask.runTaskTimer(plugin, 0L, 1L);
        leatherArmourDamageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getIsEnabled()) stopDecayTask();

                Bukkit.getOnlinePlayers().forEach(player -> applyDamageIfLeatherArmour(player));
            }
        };
        leatherArmourDamageTask.runTaskTimer(plugin, 1L, TemperatureConstants.VANILLA_DAMAGE_FREEZE_TICKS);
    }

    public void stopDecayTask() {
        if (decayTask == null) return;
        if (leatherArmourDamageTask == null) return;

        if (!decayTask.isCancelled())
            decayTask.cancel();
        if (!leatherArmourDamageTask.isCancelled())
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
                    .getOrDefault(Material.FIRE, HeatSource.DEFAULT_CONFIGURATION).heat();
            fireValue *= TemperatureConstants.PLAYER_BURNING_BOOST;
            if (fractionalChange < 0)
                fractionalChange += fireValue;
            else
                fractionalChange = fireValue;
        }

        int blocksOfPowderSnow = TemperatureUtils.getAmountOfPowderSnowBlocksInPlayer(player);
        if (blocksOfPowderSnow > 0) {
            double powderSnowValue = 1d;
            powderSnowValue *= Math.pow(TemperatureConstants.BASE_POWDER_SNOW_FACTOR, blocksOfPowderSnow);
            powderSnowValue *= TemperatureConstants.PLAYER_POWDER_SNOW_BOOST;
            if (fractionalChange < 0)
                fractionalChange = powderSnowValue;
            else
                fractionalChange += powderSnowValue;
        }

        UUID playerUUID = player.getUniqueId();

        double previousActualFreezeTicks = actualPlayerFreezeTicks.get(playerUUID);
        double nextActualFreezeTick = previousActualFreezeTicks + fractionalChange;
        nextActualFreezeTick = Maths.clampPositiveIntD(nextActualFreezeTick);
        actualPlayerFreezeTicks.put(playerUUID, nextActualFreezeTick);

        int freezeTicks = updatePlayerFreezingPoints(player, (int) nextActualFreezeTick);

        if (plugin.isDebug())
            AdminDebug.printFreezeTicks(player, freezeTicks, fractionalChange, nextActualFreezeTick);
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
