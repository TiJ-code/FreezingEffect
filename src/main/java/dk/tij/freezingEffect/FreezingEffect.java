package dk.tij.freezingEffect;

import dk.tij.freezingEffect.events.PlayerJoinListener;
import dk.tij.freezingEffect.events.PlayerQuitListener;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class FreezingEffect extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private TemperatureManager temperatureManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");
        saveDefaultConfig();

        resourceHandler = new ResourceHandler(this);
        PlayerDataHandler playerDataHandler = new PlayerDataHandler(this);

        temperatureManager = new TemperatureManager(this, playerDataHandler);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(temperatureManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(temperatureManager), this);

        temperatureManager.startDecayTask();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (resourceHandler == null || temperatureManager == null) return;
        resourceHandler.loadConfig();
        temperatureManager.reloadDecayTask();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");

        // TODO: REMOVE FROM PRODUCTION
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) configFile.delete();
    }
}
