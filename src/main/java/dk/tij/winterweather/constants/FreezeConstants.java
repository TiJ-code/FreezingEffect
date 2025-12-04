package dk.tij.winterweather.constants;

import org.bukkit.Material;

import java.util.Set;

public final class FreezeConstants {
    private FreezeConstants() {}

    public static boolean
        FREEZE_BLOCKS_ENABLE = true;

    public static final Set<Material> FREEZE_MATERIALS = Set.of(
            Material.IRON_BLOCK,
            Material.IRON_DOOR
    );
}
