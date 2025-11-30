package dk.tij.winterweather.commands;

import dk.tij.winterweather.WinterWeather;
import dk.tij.winterweather.commands.utils.ChatMessages;
import dk.tij.winterweather.constants.TemperatureConstants;
import dk.tij.winterweather.constants.TimeConstants;
import dk.tij.winterweather.handler.PlayerDataHandler;
import dk.tij.winterweather.handler.ResourceHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WinterCommand implements CommandExecutor {
    private final WinterWeather plugin;
    private final ResourceHandler resourceHandler;
    private final PlayerDataHandler playerDataHandler;

    public WinterCommand(WinterWeather plugin, PlayerDataHandler playerDataHandler, ResourceHandler resourceHandler) {
        this.plugin = plugin;
        this.playerDataHandler = playerDataHandler;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        if (!commandSender.hasPermission(CommandPermissions.BASIC_COMMAND)) {
            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (arguments.length == 1) {
            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_RELOAD)) {
                reloadConfig(commandSender);
            }

            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DEBUG)) {
                toggleDebug(commandSender);
            }

            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DAY_CYCLE)) {
                toggleCustomDayCycle(commandSender);
            }

            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_START)) {
                Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 3f, 0.6f)
                );

                final int counter = 5;
                for (int i = 0; i < counter; i++) {
                    final int count = counter - i;

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        audience.sendMessage(Component.text(String.valueOf(count), Style.style(TextColor.color(16733525))).decorate(TextDecoration.BOLD));
                    }, i * 20L);
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    audience.sendMessage(Component.text());
                    audience.sendMessage(Component.text());
                    audience.sendMessage(Component.text());
                    audience.sendMessage(Component.text(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "❄ Winter has started!"));
                    audience.sendMessage(ChatMessages.AUTHOR_COMPONENT);
                    audience.sendMessage(Component.text());
                    audience.sendMessage(Component.text());
                    audience.sendMessage(Component.text());
                    Bukkit.getOnlinePlayers().forEach(player ->
                            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 3f, 0.6f)
                    );
                }, (counter + 1) * 20L);
                plugin.enablePlugin(true);
            }

            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_STOP)) {
                plugin.enablePlugin(false);
                Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
                audience.sendMessage(Component.text(ChatMessages.CHAT_PREFIX + ChatColor.GREEN  + "☀ Winter has stopped!"));
            }

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

                setDebug(commandSender, turnOn ? 1 : 0);

                return true;
            }

            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DAY_CYCLE)) {
                boolean turnOn = arguments[1].equalsIgnoreCase(CommandLabels.DAY_CYCLE_ARGUMENT_ON);
                boolean turnOff = arguments[1].equalsIgnoreCase(CommandLabels.DAY_CYCLE_ARGUMENT_OFF);

                if (!turnOn && !turnOff) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter customDayCycle <on/off>");
                    return true;
                }

                setCustomDayCycle(commandSender, turnOn ? 1 : 0);

                return true;
            }
        }

        if (arguments.length >= 2 && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_CONFIG)) {
            if (arguments[1].equalsIgnoreCase(CommandLabels.CONFIG_ARGUMENT_RELOAD)) {
                if (arguments.length != 2) {
                    commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.AQUA + "Usage: /winter config reload");
                    return true;
                }

                reloadConfig(commandSender);

                return true;
            }

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

    private void reloadConfig(CommandSender commandSender) {
        plugin.reloadConfig();
        commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "Configuration reloaded.");
    }

    private void toggleDebug(CommandSender commandSender) {
        setDebug(commandSender, -1);
    }

    private void setDebug(CommandSender commandSender, int doToggle) {
        if (commandSender instanceof Player player) {
            boolean newState = (doToggle < 0) ? !playerDataHandler.loadPlayerShowDebug(player) : (doToggle == 0);
            playerDataHandler.savePlayerShowDebug(player, newState);
            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "Debug: " + (newState ? "ON" : "OFF"));
        } else {
            commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.RED + "Only players can run this command.");
        }
    }

    private void toggleCustomDayCycle(CommandSender commandSender) {
        setCustomDayCycle(commandSender, -1);
    }

    private void setCustomDayCycle(CommandSender commandSender, int doToggle) {
        boolean newState = (doToggle < 0) ? !TimeConstants.CUSTOM_DAY_CYCLE_ENABLE : (doToggle == 1);
        resourceHandler.setCustomDayCycleEnabled(newState);
        commandSender.sendMessage(ChatMessages.CHAT_PREFIX + ChatColor.GREEN + "CustomDayCycle: " + (newState ? "ON" : "OFF"));
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
