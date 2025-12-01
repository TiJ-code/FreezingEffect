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

public final class WinterWeather extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private TimeHandler timeHandler;
    private FreezeHandler freezeHandler;

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

        freezeHandler = new FreezeHandler(this);
        temperatureHandler = new TemperatureHandler(this);
        timeHandler = new TimeHandler(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);

        PluginCommand winterCommand = getCommand(CommandLabels.COMMAND_LABEL);
        if (winterCommand != null) {
            winterCommand.setExecutor(new WinterCommand(this));
            winterCommand.setTabCompleter(new WinterTabCompleter(this));
        }

        freezeHandler.start();
        temperatureHandler.startDecayTask();
        timeHandler.start();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        resourceHandler.reloadConfig();
        temperatureHandler.reloadDecayTask();
        timeHandler.reload();
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
        if (resourceHandler == null) return;
        resourceHandler.setEnabled(state);

        if (state) {
            temperatureHandler.startDecayTask();
            freezeHandler.start();
            timeHandler.start();
        } else {
            temperatureHandler.stop();
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
