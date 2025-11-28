package dk.tij.freezingEffect.utils;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

public final class ItemUtils {
    public static boolean isArmourItem(Material material) {
        if (material == null) return false;
        EquipmentSlot slot = material.getEquipmentSlot();
        return switch (slot) {
            case EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET -> true;
            default -> false;
        };
    }
}
