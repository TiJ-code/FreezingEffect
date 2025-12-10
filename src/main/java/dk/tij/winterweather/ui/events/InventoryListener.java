package dk.tij.winterweather.ui.events;

import dk.tij.winterweather.ui.CustomUI;
import dk.tij.winterweather.ui.UIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public record InventoryListener() implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        CustomUI ui = UIManager.get(event.getInventory());
        if (ui == null) return;

        event.setCancelled(true);
        ui.handleClick(player, event.getSlot());
    }
}
