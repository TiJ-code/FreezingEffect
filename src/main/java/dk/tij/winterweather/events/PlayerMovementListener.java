package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.AdhesionConstants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public record PlayerMovementListener(WinterWeather plugin) implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!AdhesionConstants.ADHESION_ENABLE) return;

        Player player = event.getPlayer();
        if (!plugin.getTouchHandler().isPlayerFrozen(player.getUniqueId())) return;

        float fromYaw = event.getFrom().getYaw();
        float toYaw = event.getTo().getYaw();

        if ((int) (fromYaw * 10) != (int) (toYaw * 10))
            event.getTo().setYaw(fromYaw);

        if (!event.getFrom().toVector().equals(event.getTo().toVector()))
            event.setTo(event.getFrom());
    }
}
