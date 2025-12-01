package dk.tij.winterweather.handler;

import dk.tij.winterweather.WinterWeather;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeHandler {
    private static FreezeHandler instance;

    private final WinterWeather plugin;
    private final ResourceHandler resourceHandler;
    private final Map<UUID, Integer> storedFreezeTicks = new HashMap<>();
    private BukkitRunnable freezeHandlerRunnable;

    public FreezeHandler(WinterWeather plugin) {
        if (instance != null)
            throw new RuntimeException("Only one allowed at runtime");
        instance = this;
        this.plugin = plugin;
        this.resourceHandler = ResourceHandler.getInstance();
    }

    public void start() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            storedFreezeTicks.put(player.getUniqueId(), player.getFreezeTicks());
        }
        freezeHandlerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!resourceHandler.isEnabled()) stop();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    int last = storedFreezeTicks.getOrDefault(player.getUniqueId(), player.getFreezeTicks());
                    int actual = player.getFreezeTicks();

                    if (actual < last) {
                        actual = last;
                    }

                    player.setFreezeTicks(actual);
                    storedFreezeTicks.put(player.getUniqueId(), actual);
                }
            }
        };
        freezeHandlerRunnable.runTaskTimer(plugin, 0, 1L);
    }

    public void stop() {
        freezeHandlerRunnable.cancel();
        storedFreezeTicks.clear();
    }

    public void updatePlayer(Player player, int freezeTicks) {
        storedFreezeTicks.put(player.getUniqueId(), freezeTicks);
    }

    public static FreezeHandler getInstance() {
        if (instance == null)
            throw new IllegalStateException(FreezeHandler.class.getSimpleName() + " is not yet initialised!");
        return instance;
    }
}
