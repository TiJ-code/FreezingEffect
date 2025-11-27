package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.handler.TemperatureHandler;
import dk.tij.freezingEffect.handler.FreezeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public record PlayerRespawnListener(TemperatureHandler temperatureHandler,
                                    FreezeHandler freezeHandler) implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        temperatureHandler.resetPlayer(player);
        freezeHandler.updatePlayer(player, 0);
    }
}
