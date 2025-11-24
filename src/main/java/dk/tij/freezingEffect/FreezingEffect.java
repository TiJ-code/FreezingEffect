package dk.tij.freezingEffect;

import org.bukkit.plugin.java.JavaPlugin;

public final class FreezingEffect extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getComponentLogger().info("[FreezingEffect] Plugin successfully loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getComponentLogger().info("[FreezingEffect] Plugin successfully unloaded!");
    }
}
