package dk.tij.winterweather.events;

import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(PlayerDataHandler playerDataHandler,
                                 TemperatureHandler temperatureHandler) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataHandler.savePlayerData(player, (int) temperatureHandler.getActualPlayerFreezeTicks(player));
    }
}
