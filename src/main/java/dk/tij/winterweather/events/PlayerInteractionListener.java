package dk.tij.winterweather.events;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.constants.AdhesionConstants;
import dk.tij.winterweather.handler.TouchHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public record PlayerInteractionListener(WinterWeather plugin) implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getResourceHandler().isEnabled()) return;
        if (!AdhesionConstants.ADHESION_ENABLE) return;
        if (event.getClickedBlock() == null) return;

        Material blockType = event.getClickedBlock().getType();
        if (!AdhesionConstants.ADHESIVE_MATERIALS.contains(blockType)) return;

        Player player = event.getPlayer();

        if (TouchHandler.isNotAbleToFreeze(player)) return;

        plugin.getTouchHandler().setPlayerAdhesive(player.getUniqueId(), true);
    }
}
