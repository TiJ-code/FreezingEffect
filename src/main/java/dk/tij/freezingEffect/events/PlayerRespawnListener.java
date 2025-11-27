package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import dk.tij.freezingEffect.handler.FreezeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    private final TemperatureManager temperatureManager;
    private final FreezeHandler freezeHandler;

    public PlayerRespawnListener(TemperatureManager temperatureManager, FreezeHandler freezeHandler) {
        this.temperatureManager = temperatureManager;
        this.freezeHandler = freezeHandler;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        temperatureManager.resetPlayer(player);
        freezeHandler.updatePlayer(player, 0);
    }
}
