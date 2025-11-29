package dk.tij.winterweather.commands;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.commands.utils.ChatMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WinterCommand implements CommandExecutor {
    private final WinterWeather plugin;

    public WinterCommand(WinterWeather plugin) {
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

        if (arguments.length == 2) {
            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DEBUG)) {
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
        }

        if (arguments.length >= 2 && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_CONFIG)) {
            if (arguments[1].equalsIgnoreCase(CommandLabels.CONFIG_ARGUMENT_GET)) {
                if (arguments.length != 3) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter config get <path>");
                    return true;
                }

                String path = arguments[2];
                Object value = plugin.getConfig().get(path);

                if (value == null) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.RED + "Path not found.");
                } else {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + path + " = " + value);
                }
                return true;
            }

            if (arguments[1].equalsIgnoreCase(CommandLabels.CONFIG_ARGUMENT_SET)) {
                if (arguments.length != 4) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter config set <path> <value>");
                    return true;
                }

                String path = arguments[2];
                String rawValue = arguments[3];

                if (plugin.getConfig().get(path) == null) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.RED + "Path not found.");
                    return true;
                }

                Object parsed = parseConfigValue(rawValue);
                if (parsed == null) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Failed to parse configuration value.");
                    return true;
                }

                plugin.getConfig().set(path, parsed);
                plugin.saveConfig();
                plugin.reloadConfig();

                commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "Updated configuration.");
                return true;
            }
        }

        commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter <reload/debug>");
        return true;
    }

    private Object parseConfigValue(String rawValue) {
        if (rawValue.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (rawValue.equalsIgnoreCase("false")) return Boolean.FALSE;

        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException ignored) {}

        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException ignored) {}

        return rawValue;
    }
}
