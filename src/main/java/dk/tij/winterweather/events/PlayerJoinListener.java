package dk.tij.winterweather.events;

import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener() implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TemperatureHandler.getInstance().registerPlayer(player, PlayerDataHandler.getInstance().loadPlayerFreezeTicks(player));
    }
}
