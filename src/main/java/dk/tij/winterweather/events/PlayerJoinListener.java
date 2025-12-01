package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(WinterWeather plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getTemperatureHandler().registerPlayer(player, plugin.getPlayerDataHandler().loadPlayerFreezeTicks(player));
    }
}
