package dk.tij.freezingEffect;

import dk.tij.freezingEffect.events.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreezingEffect extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("Plugin successfully loaded!");

        TemperatureManager tempManager = new TemperatureManager(this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(tempManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("Plugin successfully unloaded!");
    }
}
