package dk.tij.freezingEffect;

import dk.tij.freezingEffect.events.PlayerQuitListener;
import dk.tij.freezingEffect.events.PlayerRespawnListener;
import dk.tij.freezingEffect.events.PlayerJoinListener;
import dk.tij.freezingEffect.handler.FreezeHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import dk.tij.freezingEffect.handler.ResourceHandler;
import dk.tij.freezingEffect.handler.TemperatureHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class FreezingEffect extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private FreezeHandler freezeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");
        saveDefaultConfig();

        resourceHandler = new ResourceHandler(this);
        playerDataHandler = new PlayerDataHandler(this);

        freezeHandler = new FreezeHandler(this);
        freezeHandler.start();

        temperatureHandler = new TemperatureHandler(this, freezeHandler);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(temperatureHandler, freezeHandler), this);

        temperatureHandler.startDecayTask();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (resourceHandler == null || temperatureHandler == null) return;
        resourceHandler.loadConfig();
        temperatureHandler.reloadDecayTask();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        freezeHandler.stop();
        temperatureHandler.stopDecayTask();

        playerDataHandler.saveConfig();

        // TODO: REMOVE FROM PRODUCTION
        /*File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) configFile.delete();*/
    }
}
