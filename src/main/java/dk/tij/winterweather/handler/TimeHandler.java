package dk.tij.winterweather.handler;

import dk.tij.winterweather.constants.TimeConstants;
import dk.tij.winterweather.utils.Maths;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeHandler implements Listener {
    private static TimeHandler instance;

    private final JavaPlugin plugin;

    private final World world;
    private BukkitRunnable daylightTask;

    private double carry = 0;
    private long customTime = 0;
    private boolean initialised = false;

    public TimeHandler(JavaPlugin plugin) {
        if (instance != null)
            throw new RuntimeException("Only one allowed at runtime");
        instance = this;
        this.plugin = plugin;

        this.world = plugin.getServer().getWorlds().getFirst();
    }

    public void start() {
        daylightTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!TimeConstants.CUSTOM_DAY_CYCLE_ENABLE) {
                    stop();
                    return;
                }

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

                    final long time = customTime % TimeConstants.VANILLA_TICKS_PER_DAY;

                    if (time >= TimeConstants.T_SUNRISE_START && time < TimeConstants.T_SUNRISE_END) {
                        double normalised = (double) time / TimeConstants.T_SUNRISE_DURATION;
                        double t = TimeConstants.INTERPOLATION_FUNCTION.apply(normalised);
                        increment = Maths.lerp(TimeConstants.NIGHT_INCREMENT_PER_TICK,
                                               TimeConstants.DAY_INCREMENT_PER_TICK,
                                               t);
                    } else if (time >= TimeConstants.T_SUNRISE_END && time < TimeConstants.T_SUNDOWN_START) {
                        increment = TimeConstants.DAY_INCREMENT_PER_TICK;
                    } else if (time >= TimeConstants.T_SUNDOWN_START && time < TimeConstants.T_SUNDOWN_END) {
                        double normalised = (double) (time - TimeConstants.T_SUNDOWN_START) / TimeConstants.T_SUNDOWN_DURATION;
                        double t = TimeConstants.INTERPOLATION_FUNCTION.apply(normalised);
                        increment = Maths.lerp(TimeConstants.DAY_INCREMENT_PER_TICK,
                                               TimeConstants.NIGHT_INCREMENT_PER_TICK,
                                               t);
                    } else {
                        increment = TimeConstants.NIGHT_INCREMENT_PER_TICK;
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

    public void reload() {
        stop();
        start();
    }

    public static TimeHandler getInstance() {
        if (instance == null)
            throw new IllegalStateException(TimeHandler.class.getSimpleName() + " is not yet initialised!");
        return instance;
    }
}
