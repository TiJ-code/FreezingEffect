package dk.tij.winterweather.constants;

public final class TimeConstants {
    public static final int
        VANILLA_TICKS_PER_SECOND = 20,
        VANILLA_TICKS_PER_HALF_DAY = 12000,
        VANILLA_TICKS_PER_DAY = 24000,
        VANILLA_DAMAGE_FREEZE_DURATION_SECONDS = 2,
        VANILLA_TICKS_FREEZE_INTERVAL = VANILLA_DAMAGE_FREEZE_DURATION_SECONDS * VANILLA_TICKS_PER_SECOND,
        VANILLA_TOTAL_CYCLE_MINUTES = 20,
        VANILLA_DAY_PERCENTAGE = 50,
        MATH_SECONDS_PER_MINUTE = 60;

    public static boolean
        CUSTOM_DAY_CYCLE_ENABLE = false;

    public static int
        DAY_CYCLE_LENGTH_MINUTES = 20;

    public static double
        DAY_PERCENTAGE = 0.50;
}
