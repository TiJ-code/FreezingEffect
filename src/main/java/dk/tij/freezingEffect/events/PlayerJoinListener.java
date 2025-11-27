package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final PlayerDataHandler playerDataHandler;
    private final TemperatureManager temperatureManager;

    public PlayerJoinListener(PlayerDataHandler playerDataHandler, TemperatureManager temperatureManager) {
        this.playerDataHandler = playerDataHandler;
        this.temperatureManager = temperatureManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        temperatureManager.registerPlayer(player, playerDataHandler.loadPlayerData(player));
    }
}
