package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuitListener(WinterWeather plugin) implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataHandler().savePlayerFreezeTicks(player, (int) plugin.getTemperatureHandler().getActualPlayerFreezeTicks(player));
    }
}
