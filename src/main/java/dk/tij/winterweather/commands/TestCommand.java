package dk.tij.winterweather.commands;

import dk.tij.winterweather.ui.implementation.BucketSlotUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        if (arguments.length != 0) return true;

        if (commandSender instanceof Player player)
            new BucketSlotUI().open(player);

        return true;
    }
}
