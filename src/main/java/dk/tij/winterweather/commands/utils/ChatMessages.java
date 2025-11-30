package dk.tij.winterweather.commands.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

public final class ChatMessages {
    public static final String CHAT_PREFIX = "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "WinterWeather" + ChatColor.WHITE + "] ";
    public static final String AUTHOR = "§bby §f§l@§4§lTiJ_code";

    public static final Component AUTHOR_COMPONENT =
            Component.text("by ")
                    .color(NamedTextColor.AQUA)
                    .append(
                            Component.text("@")
                                    .color(NamedTextColor.WHITE)
                    )
                    .append(
                            Component.text("TiJ_code")
                                    .decorate(TextDecoration.BOLD)
                                    .decorate(TextDecoration.UNDERLINED)
                                    .color(NamedTextColor.RED)
                                    .clickEvent(ClickEvent.openUrl("https://github.com/TiJ-code"))
                                    .hoverEvent(HoverEvent.showText(
                                            Component.text("Open GitHub profile", NamedTextColor.GREEN)
                                    ))
                    );
}
