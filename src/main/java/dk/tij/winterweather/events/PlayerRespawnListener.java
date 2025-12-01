package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.FreezeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public record PlayerRespawnListener(WinterWeather plugin) implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getTemperatureHandler().resetPlayer(player);
        plugin.getFreezeHandler().updatePlayer(player, 0);
    }
}
