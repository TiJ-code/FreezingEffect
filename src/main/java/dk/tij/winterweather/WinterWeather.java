package dk.tij.winterweather;

import dk.tij.winterweather.commands.TestCommand;
import dk.tij.winterweather.commands.utils.CommandLabels;
import dk.tij.winterweather.commands.WinterCommand;
import dk.tij.winterweather.commands.utils.WinterTabCompleter;
import dk.tij.winterweather.config.ConfigMigrator;
import dk.tij.winterweather.events.*;
import dk.tij.winterweather.handler.*;
import dk.tij.winterweather.ui.events.InventoryListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class WinterWeather extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private TimeHandler timeHandler;
    private VanillaFreezeTicksHandler vanillaFreezeTicksHandler;
    private TouchHandler touchHandler;

    private List<IHandler> handlers;
    private List<ITaskHandler> taskHandlers;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        ConfigMigrator configMigrator = new ConfigMigrator(this);
        configMigrator.migrate();

        resourceHandler = new ResourceHandler(this);
        playerDataHandler = new PlayerDataHandler(this);
        vanillaFreezeTicksHandler = new VanillaFreezeTicksHandler(this);
        temperatureHandler = new TemperatureHandler(this);
        timeHandler = new TimeHandler(this);
        touchHandler = new TouchHandler(this);

        handlers = List.of(
                resourceHandler,
                playerDataHandler,
                temperatureHandler,
                timeHandler,
                vanillaFreezeTicksHandler,
                touchHandler
        );
        taskHandlers = handlers.stream().filter(h -> h instanceof ITaskHandler).map(h -> (ITaskHandler)h).toList();
        handlers.forEach(IHandler::init);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
        pluginManager.registerEvents(new PlayerRespawnListener(this), this);
        pluginManager.registerEvents(new PlayerMovementListener(this), this);
        pluginManager.registerEvents(new PlayerInteractionListener(this), this);

        pluginManager.registerEvents(new InventoryListener(), this);

        PluginCommand winterCommand = getCommand(CommandLabels.COMMAND_LABEL);
        if (winterCommand != null) {
            winterCommand.setExecutor(new WinterCommand(this));
            winterCommand.setTabCompleter(new WinterTabCompleter(this));
        }
        PluginCommand testCommand = getCommand("test");
        if (testCommand != null) {
            testCommand.setExecutor(new TestCommand());
        }

        taskHandlers.forEach(ITaskHandler::start);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (resourceHandler == null || playerDataHandler == null) return;

        resourceHandler.reloadConfig();
        playerDataHandler.reloadConfig();

        taskHandlers.forEach(ITaskHandler::restart);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        vanillaFreezeTicksHandler.stop();
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

    public VanillaFreezeTicksHandler getFreezeHandler() {
        return vanillaFreezeTicksHandler;
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

    public TouchHandler getTouchHandler() {
        return touchHandler;
    }
}
