package dk.tij.freezingEffect;

import dk.tij.freezingEffect.events.PlayerJoinListener;
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

        temperatureManager = new TemperatureManager(this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(temperatureManager), this);

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
