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
    private final WinterWeather plugin;
    private ResourceHandler resourceHandler;

    private final Set<UUID> frozenPlayers;
    private BukkitRunnable task;

    public TouchHandler(WinterWeather plugin) {
        this.plugin = plugin;
        this.frozenPlayers = new HashSet<>();
    }

    @Override
    public void init() {
        resourceHandler = plugin.getResourceHandler();
    }

    @Override
    public void start() {
        task = new  BukkitRunnable() {
            @Override
            public void run() {
                if (!resourceHandler.isEnabled()) {
                    stop();
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(p -> tickPlayerTouches(p));
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void stop() {
        if (!task.isCancelled())
            task.cancel();
        frozenPlayers.clear();
    }

    private void tickPlayerTouches(Player player) {
        if (TemperatureUtils.isPlayerFrozen(player)
                && !TemperatureUtils.isPlayerBurning(player)
                && isStandingOnFreezeMaterial(player)) {
            frozenPlayers.add(player.getUniqueId());
        } else {
            frozenPlayers.remove(player.getUniqueId());
        }
    }

    private boolean isStandingOnFreezeMaterial(Player player) {
        Location location = player.getLocation();
        Material under = location.subtract(0, 0.1, 0).getBlock().getType();

        return AdhesionConstants.ADHESIVE_MATERIALS.contains(under);
    }

    public boolean isPlayerFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }
}
