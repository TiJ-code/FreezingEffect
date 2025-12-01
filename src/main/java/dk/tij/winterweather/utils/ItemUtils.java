package dk.tij.winterweather.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class ItemUtils {
    private ItemUtils() {}

    public static boolean isArmourItem(Material material) {
        if (material == null) return false;
        EquipmentSlot slot = material.getEquipmentSlot();
        return switch (slot) {
            case EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET -> true;
            default -> false;
        };
    }

    public static boolean isWearingLeatherArmour(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType().toString().contains("LEATHER")) return true;
        }
        return false;
    }
}
