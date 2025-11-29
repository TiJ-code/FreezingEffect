package dk.tij.winterweather.config;

public final class ConfigEntries {
    private static final String P = ".";

    public static final String CONFIG_VERSION_ENTRY = "config-version";

    public static final String CATEGORY_FROST = "frost";

    public static final String ENABLED = "enable",
                               CRITICAL_FREEZING_TICKS = CATEGORY_FROST + P + "criticalFreezingTicks",
                               PLAYER_RADIUS = CATEGORY_FROST + P + "playerRadius",
                               INTERPOLATION_FUNCTION = CATEGORY_FROST + P + "interpolationFunction",
                               PLAYER_BURNING_BOOST = CATEGORY_FROST + P + "playerBurningBoost",
                               PLAYER_POWDER_SNOW_BOOST =  CATEGORY_FROST + P + "playerPowderSnowBoost",

                               SUB_CATEGORY_ISOLATION = CATEGORY_FROST + P + "isolation",
                               SUB_CATEGORY_HEAT_SOURCES = CATEGORY_FROST + P + "heatSources",

                               HEAT_SOURCE_VALUE = P + "value",
                               HEAT_SOURCE_RADIUS = P + "radius",

                               ISOLATION_MAX_POSSIBLE_ISOLATION = "maxPossibleIsolation",
                               ISOLATION_ARMOUR_PIECES = "armourPieces",

                               ISOLATION_ARMOUR_PIECE_VALUE = "value";
}
