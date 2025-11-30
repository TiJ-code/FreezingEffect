package dk.tij.winterweather.handler;

import dk.tij.winterweather.constants.TimeConstants;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeHandler implements Listener {
    private final JavaPlugin plugin;
    private final ResourceHandler resourceHandler;

    private final World world;
    private final long totalCycleTicks;
    private final long dayTicks;
    private final long nightTicks;

    private BukkitRunnable daylightTask;

    private double carry = 0;
    private long customTime = 0;
    private boolean initialised = false;

    public TimeHandler(JavaPlugin plugin, ResourceHandler resourceHandler) {
        this.plugin = plugin;
        this.resourceHandler = resourceHandler;

        this.world = plugin.getServer().getWorlds().getFirst();

        long totalMinutes = 1;
        double dayPercent = 10;
        double nightPercent = 1d - dayPercent;

        totalCycleTicks = totalMinutes * 60 * TimeConstants.VANILLA_TICKS_PER_SECOND;
        dayTicks = (long) (totalCycleTicks * dayPercent);
        nightTicks = (long) (totalMinutes * nightPercent);
    }

    public void start() {
        daylightTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!resourceHandler.isCustomDayCycleEnabled()) stop();

                Bukkit.getConsoleSender().sendMessage("updating time");

                Boolean doDaylightCycle = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE);
                boolean cycleOn = doDaylightCycle != null && doDaylightCycle;

                if (!initialised) {
                    customTime = world.getFullTime() % TimeConstants.VANILLA_TICKS_PER_DAY;
                    initialised = true;
                }

                long currentWorldTime = world.getFullTime() % TimeConstants.VANILLA_TICKS_PER_DAY;

                if (Math.abs(currentWorldTime - customTime) > 1) {
                    customTime = currentWorldTime;
                    carry = 0d;
                }

                if (cycleOn) {
                    double increment;

                    if (customTime < TimeConstants.VANILLA_TICKS_PER_HALF_DAY) {
                        increment = 40; // day increment per tick
                    } else {
                        increment = 20; // night increment per tick
                    }

                    carry += increment;

                    if (carry >= 1d) {
                        long ticksToAdd = (long) carry;
                        carry -= ticksToAdd;
                        customTime = (customTime + ticksToAdd) % TimeConstants.VANILLA_TICKS_PER_DAY;
                    }
                }

                world.setFullTime(customTime);
            }
        };
        daylightTask.runTaskTimer(plugin, 1L, 1L);
    }

    public void stop() {
        if (daylightTask != null) daylightTask.cancel();
    }

    public void restart() {
        stop();
        start();
    }
}
