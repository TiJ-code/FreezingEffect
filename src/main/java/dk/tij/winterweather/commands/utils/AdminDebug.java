package dk.tij.winterweather.commands.utils;

import org.bukkit.entity.Player;

public final class AdminDebug {
    private AdminDebug() {}

    public static void printFreezeTicks(Player player, int freezeTicks, double fractionalChange, double nextActualFreezeTick) {
        if (!player.hasPermission(CommandPermissions.BASIC_COMMAND)) return;

        player.sendMessage(String.format("FreezeTicks %3d | FractionalTarget: %3.1f | FreezePoints: %4.2f",
                freezeTicks, fractionalChange, nextActualFreezeTick));
    }
}
