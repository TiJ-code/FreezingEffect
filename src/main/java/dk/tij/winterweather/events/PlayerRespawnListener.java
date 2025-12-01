package dk.tij.winterweather.events;

import dk.tij.winterweather.handler.TemperatureHandler;
import dk.tij.winterweather.handler.FreezeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public record PlayerRespawnListener() implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        TemperatureHandler.getInstance().resetPlayer(player);
        FreezeHandler.getInstance().updatePlayer(player, 0);
    }
}
