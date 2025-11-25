package dk.tij.freezingEffect.events;

import dk.tij.freezingEffect.TemperatureManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final TemperatureManager temperatureManager;

    public PlayerJoinListener(TemperatureManager temperatureManager) {
        this.temperatureManager = temperatureManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        temperatureManager.loadPlayer(player);

        double temperature = temperatureManager.getTemperature(player);
        player.sendMessage(String.format("Your temperature is %.2f°", temperature));
    }
}
