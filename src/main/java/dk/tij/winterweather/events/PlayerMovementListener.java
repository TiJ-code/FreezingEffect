package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.AdhesionConstants;
import dk.tij.winterweather.handler.TouchHandler;
import dk.tij.winterweather.utils.Maths;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public record PlayerMovementListener(WinterWeather plugin) implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!AdhesionConstants.ADHESION_ENABLE) return;

        UUID uuid = event.getPlayer().getUniqueId();
        TouchHandler handler = plugin.getTouchHandler();

        if (handler.isAlreadyAdhesive(uuid)) {
            int preciseFromYaw = (int) (event.getFrom().getYaw() * Maths.PRECISION_TWO_DECIMALS);
            int preciseToYaw = (int) (event.getTo().getYaw() * Maths.PRECISION_TWO_DECIMALS);

            if (preciseFromYaw != preciseToYaw) {
                event.getTo().setYaw(event.getFrom().getYaw());
            }

            if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
                event.setTo(event.getFrom());
            }
        }
    }


}
