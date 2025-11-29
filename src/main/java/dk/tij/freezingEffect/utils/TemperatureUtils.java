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
        Location location = player.getLocation();
        World world = eyeLocation.getWorld();

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
                    Block block = world.getBlockAt(x, y, z);
                    Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
                    Material type = block.getType();

                    if (!TemperatureConstants.HEAT_SOURCE_WARMING.containsKey(type)) continue;
                    if (!isHeatSourceUnobstructed(eyeLocation, blockCenter)) continue;
                    if (!isPlayerInHeatSourceRange(type, location, blockCenter)) continue;

                    result += TemperatureConstants.HEAT_SOURCE_WARMING
                            .getOrDefault(type, HeatSource.DEFAULT_CONFIGURATION).heat();
                }
            }
        }

        return (result > 0) ? result : -1;
    }

    public static boolean isPlayerBurning(Player player) {
        return player.getFireTicks() > 0;
    }

    private static boolean isHeatSourceUnobstructed(Location playerLocation, Location heatSourceLocation) {
        RayTraceResult result = playerLocation.getWorld().rayTraceBlocks(
                playerLocation,
                heatSourceLocation.toVector().subtract(playerLocation.toVector()),
                playerLocation.distance(heatSourceLocation),
                FluidCollisionMode.NEVER,
                true
        );

        if (result == null) return true;

        return result.getHitBlock() != null && TemperatureConstants.HEAT_SOURCE_WARMING.containsKey(result.getHitBlock().getType());
    }

    private static boolean isPlayerInHeatSourceRange(Material heatSourceType, Location playerLocation, Location heatSourceLocation) {
        int heatSourceRange = TemperatureConstants.HEAT_SOURCE_WARMING
                .getOrDefault(heatSourceType, HeatSource.DEFAULT_CONFIGURATION).range();

        return playerLocation.distance(heatSourceLocation) <= (double) heatSourceRange;
    }
}
