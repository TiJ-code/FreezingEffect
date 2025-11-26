package dk.tij.freezingEffect;

import dk.tij.freezingEffect.events.PlayerJoinListener;
import dk.tij.freezingEffect.events.PlayerQuitListener;
import dk.tij.freezingEffect.handler.FreezeHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class FreezingEffect extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private TemperatureManager temperatureManager;
    private FreezeHandler freezeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");
        saveDefaultConfig();

        resourceHandler = new ResourceHandler(this);
        PlayerDataHandler playerDataHandler = new PlayerDataHandler(this);

        freezeHandler = new FreezeHandler(this);
        freezeHandler.start();

        temperatureManager = new TemperatureManager(this, playerDataHandler, freezeHandler);
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

        freezeHandler.stop();
        temperatureManager.stopDecayTask();

        for (Player player : getServer().getOnlinePlayers()) {
            temperatureManager.savePlayer(player);
        }

        // TODO: REMOVE FROM PRODUCTION
        File configFile = new File(getDataFolder(), "config.yml");
        if (configFile.exists()) configFile.delete();
    }
}
