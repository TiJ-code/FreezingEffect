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
import org.bukkit.util.Vector;

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

        int heatRadius = (int) Math.round(TemperatureConstants.HEAT_RADIUS);
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
                    if (!isBlockInRadius(location, blockCenter)) continue;
                    if (!isPlayerInHeatSourceRange(type, location, blockCenter)) continue;
                    if (!isHeatSourceUnobstructed(eyeLocation, blockCenter)) continue;

                    result += TemperatureConstants.HEAT_SOURCE_WARMING.get(type).heat();
                }
            }
        }

        return (result > 0) ? result : -1;
    }

    public static boolean isPlayerBurning(Player player) {
        return player.getFireTicks() > 0;
    }

    public static boolean isPlayerInPowderSnow(Player player) {
        return player.getEyeLocation().getBlock().getType() == Material.POWDER_SNOW
                || player.getLocation().getBlock().getType() == Material.POWDER_SNOW;
    }

    private static boolean isHeatSourceUnobstructed(Location playerLocation, Location heatSourceLocation) {
        Vector direction = heatSourceLocation.toVector().subtract(playerLocation.toVector());
        double distance = direction.length();

        RayTraceResult result = playerLocation.getWorld().rayTraceBlocks(
                playerLocation,
                direction.normalize(),
                distance,
                FluidCollisionMode.NEVER,
                true
        );

        if (result == null) return true;

        Block hit = result.getHitBlock();
        return hit != null && TemperatureConstants.HEAT_SOURCE_WARMING.containsKey(hit.getType());
    }

    private static boolean isPlayerInHeatSourceRange(Material heatSourceType, Location playerLocation, Location heatSourceLocation) {
        double heatSourceRadiusSquared = TemperatureConstants.HEAT_SOURCE_WARMING
                .getOrDefault(heatSourceType, HeatSource.DEFAULT_CONFIGURATION).radiusSquared();

        return playerLocation.distanceSquared(heatSourceLocation) < heatSourceRadiusSquared;
    }

    private static boolean isBlockInRadius(Location playerLocation, Location blockLocation) {
        return playerLocation.distanceSquared(blockLocation) <= TemperatureConstants.HEAT_RADIUS_SQUARED;
    }
}
