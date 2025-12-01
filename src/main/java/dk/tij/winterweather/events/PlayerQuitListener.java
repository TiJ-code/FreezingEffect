package dk.tij.winterweather.events;

import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener() implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerDataHandler.getInstance().savePlayerFreezeTicks(player, (int) TemperatureHandler.getInstance().getActualPlayerFreezeTicks(player));
    }
}
