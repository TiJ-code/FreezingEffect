package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.handler.TemperatureHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(PlayerDataHandler playerDataHandler,
                                 TemperatureHandler temperatureHandler) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataHandler.savePlayerData(player, temperatureHandler.getPlayerFreezePoints(player));
    }
}
