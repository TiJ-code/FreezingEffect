package dk.tij.winterweather.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public record UIButton(ItemStack item, Consumer<Player> action) {
    public void click(Player player) {
        if (action != null)
            action.accept(player);
    }
}
