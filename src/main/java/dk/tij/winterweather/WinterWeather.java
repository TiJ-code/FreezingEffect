package dk.tij.winterweather;

import dk.tij.winterweather.commands.CommandLabels;
import dk.tij.winterweather.commands.WinterCommand;
import dk.tij.winterweather.commands.utils.WinterTabCompleter;
import dk.tij.winterweather.config.ConfigMigrator;
import dk.tij.winterweather.events.PlayerQuitListener;
import dk.tij.winterweather.events.PlayerRespawnListener;
import dk.tij.winterweather.events.PlayerJoinListener;
import dk.tij.winterweather.handler.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class WinterWeather extends JavaPlugin {
    private final ResourceHandler resourceHandler;
    private final PlayerDataHandler playerDataHandler;
    private final TemperatureHandler temperatureHandler;
    private final TimeHandler timeHandler;
    private final FreezeHandler freezeHandler;

    private final List<IHandler> handlers;
    private final List<ITaskHandler> taskHandlers;

    public WinterWeather() {
        this.resourceHandler = new ResourceHandler(this);
        this.playerDataHandler = new PlayerDataHandler(this);
        this.freezeHandler = new FreezeHandler(this);
        this.temperatureHandler = new TemperatureHandler(this);
        this.timeHandler = new TimeHandler(this);

        this.handlers = List.of(
                resourceHandler,
                playerDataHandler,
                temperatureHandler,
                timeHandler,
                freezeHandler
        );
        this.taskHandlers = handlers.stream().filter(h -> h instanceof ITaskHandler).map(h -> (ITaskHandler)h).toList();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        ConfigMigrator configMigrator = new ConfigMigrator(this);
        configMigrator.migrate();

        handlers.forEach(IHandler::init);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this), this);

        PluginCommand winterCommand = getCommand(CommandLabels.COMMAND_LABEL);
        if (winterCommand != null) {
            winterCommand.setExecutor(new WinterCommand(this));
            winterCommand.setTabCompleter(new WinterTabCompleter(this));
        }

        freezeHandler.start();
        temperatureHandler.start();
        timeHandler.start();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        resourceHandler.reloadConfig();
        playerDataHandler.reloadConfig();

        taskHandlers.forEach(ITaskHandler::restart);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        freezeHandler.stop();
        temperatureHandler.stop();

        playerDataHandler.saveConfig();
    }

    public void enablePlugin(boolean state) {
        resourceHandler.setEnabled(state);

        if (state)
            taskHandlers.forEach(ITaskHandler::start);
        else
            taskHandlers.forEach(ITaskHandler::stop);
    }

    public void enableCustomDayCycle(boolean state) {
        if (state)
            timeHandler.start();
        else
            timeHandler.stop();
    }

    public FreezeHandler getFreezeHandler() {
        return freezeHandler;
    }

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public TemperatureHandler getTemperatureHandler() {
        return temperatureHandler;
    }

    public TimeHandler getTimeHandler() {
        return timeHandler;
    }
}
