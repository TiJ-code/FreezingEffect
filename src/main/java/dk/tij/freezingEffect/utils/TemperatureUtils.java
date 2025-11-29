package dk.tij.freezingEffect.utils;

import dk.tij.freezingEffect.constants.TemperatureConstants;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public final class TemperatureUtils {
    public static double getLeatherReduction(Player player) {
        double reduction = 0d;

        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;

            Material type = item.getType();

            reduction += TemperatureConstants.ARMOUR_PIECE_ISOLATION.getOrDefault(type, 0d);
        }

        return reduction * TemperatureConstants.MAX_POSSIBLE_ISOLATION;
    }

    public static double getNumberOfHeatSourcesNearby(Player player) {
        double result = 0;

        int heatRadius = TemperatureConstants.HEAT_RADIUS;
        Location eyeLocation = player.getEyeLocation();
        World world = eyeLocation.getWorld();

        final int blockX = eyeLocation.getBlockX(),
                  blockY = eyeLocation.getBlockY(),
                  blockZ = eyeLocation.getBlockZ();

        final int maxX = blockX + heatRadius,
                  maxY = blockY + heatRadius,
                  maxZ = blockZ + heatRadius;
        final int minX = blockX - heatRadius,
                  minY = blockY - heatRadius,
                  minZ = blockZ - heatRadius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material type = block.getType();

                    if (!TemperatureConstants.HEAT_SOURCE_WARMING.containsKey(type)
                        || !canSeeHeatSource(eyeLocation, block)) continue;

                    result += TemperatureConstants.HEAT_SOURCE_WARMING.getOrDefault(type, 1d);
                }
            }
        }

        return (result > 0) ? result : -1;
    }

    private static boolean canSeeHeatSource(Location from, Block target) {
        Location center = target.getLocation().add(0.5, 0.5, 0.5);

        RayTraceResult result = from.getWorld().rayTraceBlocks(
                from,
                center.toVector().subtract(from.toVector()),
                from.distance(center),
                FluidCollisionMode.NEVER,
                true
        );

        if (result == null) return true;

        return result.getHitBlock() != null && result.getHitBlock().equals(target);
    }
}
