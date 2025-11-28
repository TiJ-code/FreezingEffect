package dk.tij.freezingEffect.utils;

import dk.tij.freezingEffect.constants.TemperatureConstants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class TemperatureUtils {
    public static double getLeatherReduction(Player player) {
        double reduction = 0d;

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;

            Material type = item.getType();

            if (type.toString().contains("LEATHER")) reduction += 0.20;
        }

        return Math.min(reduction, 0.8);
    }

    public static boolean isNearHeatSource(Player player) {
        int heatRadius = TemperatureConstants.HEAT_RADIUS;
        Location location = player.getLocation();

        final int blockX = location.getBlockX(),
                blockY = location.getBlockY(),
                blockZ = location.getBlockZ();

        final int maxX = blockX + heatRadius,
                maxY = blockY + heatRadius,
                maxZ = blockZ + heatRadius;
        final int minX = blockX - heatRadius,
                minY = blockY - heatRadius,
                minZ = blockZ - heatRadius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    Material type = block.getType();
                    if (TemperatureConstants.HEAT_SOURCES.contains(type)) return true;
                }
            }
        }

        return false;
    }
}
