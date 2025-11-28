package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.handler.TemperatureHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public record PlayerJoinListener(PlayerDataHandler playerDataHandler,
                                 TemperatureHandler temperatureHandler) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        temperatureHandler.registerPlayer(player, playerDataHandler.loadPlayerData(player));
    }
}
