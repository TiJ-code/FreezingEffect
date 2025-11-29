package dk.tij.freezingEffect.utils;

public record HeatSource(double heat, int range) {
    public static final HeatSource DEFAULT_CONFIGURATION = new HeatSource(1d, 3);
}
