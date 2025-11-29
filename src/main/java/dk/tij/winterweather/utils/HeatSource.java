package dk.tij.winterweather.utils;

public record HeatSource(double heat, double radiusSquared) {
    public static final HeatSource DEFAULT_CONFIGURATION = new HeatSource(1, 3*3);
}
