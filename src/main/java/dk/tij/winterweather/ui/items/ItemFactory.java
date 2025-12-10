package dk.tij.winterweather.ui.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {
    public static ItemStack modelItem(int modelId){
        ItemStack item = new ItemStack(Material.PAPER);

        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(modelId);
        meta.setDisplayName(" ");
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack button(String text, int modelId) {
        ItemStack item = modelItem(modelId);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(text);
        item.setItemMeta(meta);

        return item;
    }
}
