package dk.tij.freezingEffect;

import dk.tij.freezingEffect.commands.CommandLabels;
import dk.tij.freezingEffect.commands.WinterCommand;
import dk.tij.freezingEffect.commands.utils.WinterTabCompleter;
import dk.tij.freezingEffect.events.PlayerQuitListener;
import dk.tij.freezingEffect.events.PlayerRespawnListener;
import dk.tij.freezingEffect.events.PlayerJoinListener;
import dk.tij.freezingEffect.handler.FreezeHandler;
import dk.tij.freezingEffect.handler.PlayerDataHandler;
import dk.tij.freezingEffect.handler.ResourceHandler;
import dk.tij.freezingEffect.handler.TemperatureHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreezingEffect extends JavaPlugin {
    private ResourceHandler resourceHandler;
    private PlayerDataHandler playerDataHandler;
    private TemperatureHandler temperatureHandler;
    private FreezeHandler freezeHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getComponentLogger().info("Plugin successfully loaded!");

        resourceHandler = new ResourceHandler(this);
        playerDataHandler = new PlayerDataHandler(this);

        freezeHandler = new FreezeHandler(this);

        temperatureHandler = new TemperatureHandler(this, freezeHandler);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerDataHandler, temperatureHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(temperatureHandler, freezeHandler), this);

        getCommand(CommandLabels.COMMAND_LABEL).setExecutor(new WinterCommand(this));
        getCommand(CommandLabels.COMMAND_LABEL).setTabCompleter(new WinterTabCompleter());

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
}
