package dk.tij.freezingEffect.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeHandler {
    private final JavaPlugin plugin;
    private final Map<UUID, Integer> storedFreezeTicks = new HashMap<>();
    private final BukkitRunnable freezeHandlerRunnable;

    public FreezeHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.freezeHandlerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
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
    }

    public void start() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            storedFreezeTicks.put(player.getUniqueId(), player.getFreezeTicks());
        }
        freezeHandlerRunnable.runTaskTimer(plugin, 1L, 1L);
    }

    public void stop() {
        freezeHandlerRunnable.cancel();
        storedFreezeTicks.clear();
    }

    public void updatePlayer(Player player, int freezeTicks) {
        storedFreezeTicks.put(player.getUniqueId(), freezeTicks);
    }
}
