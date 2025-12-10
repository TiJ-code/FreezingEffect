package dk.tij.winterweather.ui.implementation;

import dk.tij.winterweather.ui.CustomUI;
import dk.tij.winterweather.ui.UIButton;
import dk.tij.winterweather.ui.items.ItemFactory;
import org.bukkit.inventory.ItemStack;

public class BucketSlotUI extends CustomUI {
    public BucketSlotUI() {
        super("Bucket Slot UI", 27);

        ItemStack background = ItemFactory.modelItem(2000);
        for (int i = 0; i < 27; i++) inventory.setItem(i, background);

        setButton(11, new UIButton(
                ItemFactory.button("§fChange type", 2001),
                player -> player.sendMessage("Change type pressed!")
        ));

        setButton(15, new UIButton(
                ItemFactory.button("§fToggle lock", 2002),
                player -> player.sendMessage("Toggle lock pressed")
        ));

        setButton(13, new UIButton(
                ItemFactory.modelItem(2003),
                player -> player.sendMessage("Bucket clicked")
        ));
    }
}
