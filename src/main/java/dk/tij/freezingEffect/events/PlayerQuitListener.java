package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final TemperatureManager temperatureManager;
    private final PlayerDataHandler playerDataHandler;

    public PlayerQuitListener(PlayerDataHandler playerDataHandler, TemperatureManager temperatureManager) {
        this.temperatureManager = temperatureManager;
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataHandler.savePlayerData(player, temperatureManager.getPlayerFreezePoints(player));
    }
}
