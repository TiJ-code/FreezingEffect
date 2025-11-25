package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final TemperatureManager temperatureManager;

    public PlayerQuitListener(TemperatureManager temperatureManager) {
        this.temperatureManager = temperatureManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        temperatureManager.savePlayer(player);
    }
}
