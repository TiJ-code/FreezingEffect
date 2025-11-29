package dk.tij.freezingEffect.commands;

import dk.tij.freezingEffect.FreezingEffect;
import dk.tij.freezingEffect.commands.utils.ChatMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WinterCommand implements CommandExecutor {
    private final FreezingEffect plugin;

    public WinterCommand(FreezingEffect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        if (!commandSender.hasPermission(CommandPermissions.BASIC_COMMAND)) {
            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (arguments.length == 1 && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_RELOAD)) {
            plugin.reloadConfig();
            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "Configuration reloaded.");
            return true;
        }

        if (arguments.length == 2 && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DEBUG)) {
            boolean turnOn = arguments[1].equalsIgnoreCase(CommandLabels.DEBUG_ARGUMENT_ON);
            boolean turnOff = arguments[1].equalsIgnoreCase(CommandLabels.DEBUG_ARGUMENT_OFF);

            if (!turnOn && !turnOff) {
                commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter debug <on/off>");
                return true;
            }

            plugin.setDebug(turnOn);

            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "Debug: " + (turnOn ? "ON" : "OFF"));

            return true;
        }

        commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter <reload/debug>");
        return true;
    }
}
