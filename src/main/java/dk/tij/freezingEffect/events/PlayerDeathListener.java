package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import dk.tij.freezingEffect.handler.FreezeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final TemperatureManager temperatureManager;
    private final FreezeHandler freezeHandler;

    public PlayerDeathListener(TemperatureManager temperatureManager, FreezeHandler freezeHandler) {
        this.temperatureManager = temperatureManager;
        this.freezeHandler = freezeHandler;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        temperatureManager.setTemperature(player, 0);
        freezeHandler.updatePlayer(player, 0);
    }
}
