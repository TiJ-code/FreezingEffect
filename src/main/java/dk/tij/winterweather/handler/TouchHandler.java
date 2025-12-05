package dk.tij.winterweather.handler;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.AdhesionConstants;
import dk.tij.winterweather.utils.TemperatureUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TouchHandler implements IHandler, ITaskHandler {
    public static TouchHandler instance;

    private final WinterWeather plugin;
    private ResourceHandler resourceHandler;

    private final Set<UUID> frozenPlayers;
    private BukkitRunnable task;

    public TouchHandler(WinterWeather plugin) {
        if (instance != null)
            throw new RuntimeException("Cannot create more than one instance of TouchHandler");
        instance = this;
        this.plugin = plugin;
        this.frozenPlayers = new HashSet<>();
    }

    @Override
    public void init() {
        resourceHandler = plugin.getResourceHandler();
    }

    @Override
    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!resourceHandler.isEnabled() || !AdhesionConstants.ADHESION_ENABLE) {
                    stop();
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(instance::tickPlayer);
            }
        };
        task.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void stop() {
        if (task.isCancelled()) task.cancel();
    }

    private void tickPlayer(Player player) {
        UUID userUUID = player.getUniqueId();

        if (isNotAbleToFreeze(player))
            frozenPlayers.remove(userUUID);
        else
            if (TemperatureUtils.isPlayerFrozen(player) && isStandingOnAdhesiveMaterial(player))
                frozenPlayers.add(userUUID);
    }

    public boolean isStandingOnAdhesiveMaterial(Player player) {
        Location location = player.getLocation();
        Material under = location.subtract(0, 0.1, 0).getBlock().getType();

        return AdhesionConstants.ADHESIVE_MATERIALS.contains(under);
    }

    public boolean isAlreadyAdhesive(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public void setPlayerAdhesive(UUID uuid, boolean frozen) {
        if (frozen)
            frozenPlayers.add(uuid);
        else
            frozenPlayers.remove(uuid);
    }

    public static boolean isNotAbleToFreeze(Player player) {
        return !TemperatureUtils.isPlayerFrozen(player)
                || TemperatureUtils.isPlayerBurning(player)
                || !(TemperatureUtils.getNumberOfHeatSourcesNearby(player) <= 0);
    }
}
