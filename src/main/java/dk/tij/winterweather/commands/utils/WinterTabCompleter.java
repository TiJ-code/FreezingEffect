package dk.tij.winterweather.commands.utils;

import dk.tij.winterweather.WinterWeather;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class WinterTabCompleter implements TabCompleter {
    private final WinterWeather plugin;

    public WinterTabCompleter(WinterWeather plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        // /winter <reload/debug/config>
        if (arguments.length == 1) {
            return List.of(
                    CommandLabels.ARGUMENT_RELOAD,
                    CommandLabels.ARGUMENT_DEBUG,
                    CommandLabels.ARGUMENT_CONFIG,
                    CommandLabels.ARGUMENT_START,
                    CommandLabels.ARGUMENT_STOP,
                    CommandLabels.ARGUMENT_DAY_CYCLE
            );
        }

        if (arguments.length == 2) {
            // /winter debug <on/off>
            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DEBUG)) {
                return List.of(CommandLabels.DEBUG_ARGUMENT_ON, CommandLabels.DEBUG_ARGUMENT_OFF);
            }
            // /winter customDayCycle <on/off>
            if (arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_DAY_CYCLE)) {
                return List.of(CommandLabels.DAY_CYCLE_ARGUMENT_ON, CommandLabels.DAY_CYCLE_ARGUMENT_OFF);
            }
        }

        // /winter config <get/set/reload>
        if (arguments.length == 2 && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_CONFIG)) {
            return List.of(CommandLabels.CONFIG_ARGUMENT_GET, CommandLabels.CONFIG_ARGUMENT_SET, CommandLabels.CONFIG_ARGUMENT_RELOAD);
        }

        // /winter config get <path...>
        if (arguments.length == 3
                && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_CONFIG)
                && arguments[1].equalsIgnoreCase(CommandLabels.CONFIG_ARGUMENT_GET)) {

            return getMatchingConfigPaths(arguments[2]);
        }

        // /winter config set <path> <value>
        if (arguments.length == 3
                && arguments[0].equalsIgnoreCase(CommandLabels.ARGUMENT_CONFIG)
                && arguments[1].equalsIgnoreCase(CommandLabels.CONFIG_ARGUMENT_SET)) {

            return getMatchingConfigPaths(arguments[2]);
        }

        // /winter config set <path> <value>
        // Suggest nothing, user types free-form value
        return Collections.emptyList();
    }

    private List<String> getMatchingConfigPaths(String typed) {
        Set<String> keys = plugin.getConfig().getKeys(true);
        List<String> suggestions = new ArrayList<>();

        for (String key : keys) {
            if (key.startsWith(typed)) {
                suggestions.add(key);
            }
        }

        return suggestions;
    }
}
