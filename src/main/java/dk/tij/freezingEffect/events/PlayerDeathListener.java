package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final TemperatureManager temperatureManager;

    public PlayerDeathListener(TemperatureManager temperatureManager) {
        this.temperatureManager = temperatureManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        temperatureManager.setTemperature(player, 0);
        player.sendMessage("Temperature is back to WARM");
    }
}
