package dk.tij.winterweather;

import dk.tij.winterweather.commands.CommandLabels;
import dk.tij.winterweather.commands.WinterCommand;
import dk.tij.winterweather.commands.utils.WinterTabCompleter;
import dk.tij.winterweather.config.ConfigMigrator;
import dk.tij.winterweather.events.PlayerQuitListener;
import dk.tij.winterweather.events.PlayerRespawnListener;
import dk.tij.winterweather.events.PlayerJoinListener;
import dk.tij.winterweather.handler.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class WinterWeather extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private TimeHandler timeHandler;
    private FreezeHandler freezeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        ConfigMigrator configMigrator = new ConfigMigrator(this);

        getComponentLogger().info("Plugin successfully loaded!");

        configMigrator.migrate();

        resourceHandler = new ResourceHandler(this);
        playerDataHandler = new PlayerDataHandler(this);

        freezeHandler = new FreezeHandler(this, resourceHandler);

        temperatureHandler = new TemperatureHandler(this, resourceHandler, freezeHandler, playerDataHandler);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(temperatureHandler, freezeHandler), this);

        getCommand(CommandLabels.COMMAND_LABEL).setExecutor(new WinterCommand(this, playerDataHandler, resourceHandler));
        getCommand(CommandLabels.COMMAND_LABEL).setTabCompleter(new WinterTabCompleter(this));

        timeHandler = new TimeHandler(this, resourceHandler);

        freezeHandler.start();
        temperatureHandler.startDecayTask();
        timeHandler.start();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (resourceHandler == null) return;
        resourceHandler.reloadConfig();
        if (temperatureHandler == null) return;
        temperatureHandler.reloadDecayTask();
        if (timeHandler == null) return;
        timeHandler.reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        if (freezeHandler != null)
            freezeHandler.stop();
        if (temperatureHandler != null)
            temperatureHandler.stopDecayTask();

        if (playerDataHandler != null)
            playerDataHandler.saveConfig();
    }

    public void enablePlugin(boolean state) {
        if (resourceHandler == null) return;
        resourceHandler.setEnabled(state);

        if (state) {
            temperatureHandler.startDecayTask();
            freezeHandler.start();
            timeHandler.start();
        } else {
            temperatureHandler.stopDecayTask();
            freezeHandler.stop();
            timeHandler.stop();
        }
    }

    public void enableCustomDayCycle(boolean state) {
        if (timeHandler == null) return;

        if (state)
            timeHandler.start();
        else
            timeHandler.stop();
    }
}
