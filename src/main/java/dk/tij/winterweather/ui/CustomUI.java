package dk.tij.winterweather.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.swing.*;

public abstract class CustomUI {
    protected final Inventory inventory;
    protected final UIButton[] buttons;

    public CustomUI(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
        this.buttons = new UIButton[size];
    }

    public void setButton(int slot, UIButton button) {
        buttons[slot] = button;
        inventory.setItem(slot, button.getItem());
    }

    public void open(Player player) {
        player.openInventory(inventory);
        UIManager.register(this);
    }

    public void handleClick(Player player, int slot) {
        if (slot < 0 || slot >= buttons.length) throw new IndexOutOfBoundsException();

        UIButton button = buttons[slot];

        if (button != null)
            button.click(player);
    }
}
