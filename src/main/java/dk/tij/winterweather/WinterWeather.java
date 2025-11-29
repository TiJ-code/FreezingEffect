package dk.tij.winterweather;

import dk.tij.winterweather.commands.CommandLabels;
import dk.tij.winterweather.commands.WinterCommand;
import dk.tij.winterweather.commands.utils.WinterTabCompleter;
import dk.tij.winterweather.config.ConfigMigrator;
import dk.tij.winterweather.events.PlayerQuitListener;
import dk.tij.winterweather.events.PlayerRespawnListener;
import dk.tij.winterweather.events.PlayerJoinListener;
import dk.tij.winterweather.handler.FreezeHandler;
import dk.tij.winterweather.handler.PlayerDataHandler;
import dk.tij.winterweather.handler.ResourceHandler;
import dk.tij.winterweather.handler.TemperatureHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class WinterWeather extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private FreezeHandler freezeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getComponentLogger().info("Plugin successfully loaded!");

        new ConfigMigrator(this).migrate();

        resourceHandler = new ResourceHandler(this);
        playerDataHandler = new PlayerDataHandler(this);

        freezeHandler = new FreezeHandler(this);

        temperatureHandler = new TemperatureHandler(this, freezeHandler);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(temperatureHandler, freezeHandler), this);

        getCommand(CommandLabels.COMMAND_LABEL).setExecutor(new WinterCommand(this));
        getCommand(CommandLabels.COMMAND_LABEL).setTabCompleter(new WinterTabCompleter(this));

        freezeHandler.start();
        temperatureHandler.startDecayTask();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (resourceHandler == null) return;
        resourceHandler.reloadConfig();
        if (temperatureHandler == null) return;
        temperatureHandler.reloadDecayTask();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        freezeHandler.stop();
        temperatureHandler.stopDecayTask();

        playerDataHandler.saveConfig();
    }

    public void setDebug(boolean debug) {
        if (resourceHandler == null) return;
        resourceHandler.setDebug(debug);
    }

    public boolean isDebug() {
        if (resourceHandler == null) return false;
        return resourceHandler.isDebug();
    }

    public void setIsEnabled(boolean state) {
        if (resourceHandler == null) return;
        resourceHandler.setEnabled(state);

        if (state) {
            temperatureHandler.startDecayTask();
            freezeHandler.start();
        } else {
            temperatureHandler.stopDecayTask();
            freezeHandler.stop();
        }
    }

    public boolean getIsEnabled() {
        if (resourceHandler == null) return false;
        return resourceHandler.isEnabled();
    }
}
