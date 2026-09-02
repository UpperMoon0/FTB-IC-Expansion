package com.nstut.ftbice.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratorDefaultsTest {
    @Test
    void legacyDefaultsRemainStable() {
        assertEquals(30, GeneratorDefaults.advancedGeneratorOutput(GeneratorDefaults.ADVANCED_GENERATOR_MULTIPLIER));
        assertEquals(60, GeneratorDefaults.ADVANCED_GEOTHERMAL_OUTPUT);
    }
}
