package com.nstut.ftbice.shared;

public final class GeneratorDefaults {
    public static final double ADVANCED_GENERATOR_MULTIPLIER = 3.0D;
    public static final int BASE_GENERATOR_OUTPUT = 10;
    public static final int ADVANCED_GEOTHERMAL_OUTPUT = 60;

    private GeneratorDefaults() {
    }

    public static int advancedGeneratorOutput(double multiplier) {
        return (int) (BASE_GENERATOR_OUTPUT * multiplier);
    }
}
