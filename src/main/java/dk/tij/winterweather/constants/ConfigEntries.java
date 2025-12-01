package dk.tij.winterweather.constants;

public final class ConfigEntries {
    private ConfigEntries() {}

    private static final String P = ".";

    public static final int CURRENT_VERSION = 7; // TODO
    public static final String CONFIG_VERSION_ENTRY = "config-version";

    public static final String CATEGORY_FROST = "frost",
                               CATEGORY_DAYLIGHT = "days";

    public static final String ENABLED = "enable",

                               DAYLIGHT_CUSTOM_CYCLE_ENABLE = CATEGORY_DAYLIGHT + P + "customCycleEnable",
                               DAYLIGHT_TOTAL_CYCLE_MINUTES = CATEGORY_DAYLIGHT + P + "totalCycleMinutes",
                               DAYLIGHT_DAY_PERCENTAGE = CATEGORY_DAYLIGHT + P + "dayPercentage",
                               DAYLIGHT_INTERPOLATION_FUNCTION = CATEGORY_DAYLIGHT + P + "interpolationFunction",
                               DAYLIGHT_T_SUNRISE_START = CATEGORY_DAYLIGHT + P + "t_sunrise_start",
                               DAYLIGHT_T_SUNRISE_END = CATEGORY_DAYLIGHT + P + "t_sunrise_end",
                               DAYLIGHT_T_SUNDOWN_START = CATEGORY_DAYLIGHT + P + "t_sundown_start",
                               DAYLIGHT_T_SUNDOWN_END = CATEGORY_DAYLIGHT + P + "t_sundown_end",

                               FROST_CRITICAL_FREEZING_TICKS = CATEGORY_FROST + P + "criticalFreezingTicks",
                               FROST_PLAYER_RADIUS = CATEGORY_FROST + P + "playerRadius",
                               FROST_INTERPOLATION_FUNCTION = CATEGORY_FROST + P + "interpolationFunction",
                               FROST_PLAYER_BURNING_BOOST = CATEGORY_FROST + P + "playerBurningBoost",
                               FROST_PLAYER_POWDER_SNOW_BOOST =  CATEGORY_FROST + P + "playerPowderSnowBoost",

                               FROST_SUB_CATEGORY_ISOLATION = CATEGORY_FROST + P + "isolation",
                               FROST_SUB_CATEGORY_HEAT_SOURCES = CATEGORY_FROST + P + "heatSources",

                               FROST_HEAT_SOURCE_VALUE = P + "value",
                               FROST_HEAT_SOURCE_RADIUS = P + "radius",

                               FROST_ISOLATION_MAX_POSSIBLE_ISOLATION = "maxPossibleIsolation",
                               FROST_ISOLATION_ARMOUR_PIECES = "armourPieces",

                               FROST_ISOLATION_ARMOUR_PIECE_VALUE = "value";
}
