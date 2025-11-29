package dk.tij.freezingEffect.commands.utils;

import dk.tij.freezingEffect.commands.CommandLabels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class WinterTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        if (arguments.length == 1) {
            return List.of(CommandLabels.ARGUMENT_RELOAD, CommandLabels.ARGUMENT_DEBUG);
        }

        if (arguments.length == 2 && arguments[0].equals(CommandLabels.ARGUMENT_DEBUG)) {
            return List.of(CommandLabels.DEBUG_ARGUMENT_ON, CommandLabels.DEBUG_ARGUMENT_OFF);
        }

        return Collections.emptyList();
    }
}
