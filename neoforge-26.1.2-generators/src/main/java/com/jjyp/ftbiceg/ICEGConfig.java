package com.jjyp.ftbiceg;

import com.nstut.ftbice.shared.GeneratorDefaults;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ICEGConfig {
    public static final Machines MACHINES;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        MACHINES = new Machines(builder);
        COMMON_SPEC = builder.build();
    }

    public static final class Machines {
        public final ModConfigSpec.DoubleValue ADVANCED_GENERATOR_CAPACITY;
        public final ModConfigSpec.DoubleValue ADVANCED_GENERATOR_OUTPUT;
        public final ModConfigSpec.IntValue ADVANCED_GEOTHERMAL_GENERATOR_TANK_SIZE;
        public final ModConfigSpec.DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY;
        public final ModConfigSpec.DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT;

        private Machines(ModConfigSpec.Builder builder) {
            builder.push("machines");
            ADVANCED_GENERATOR_CAPACITY = builder.defineInRange("advanced_generator_capacity", 12_000D, 1D, 100_000D);
            ADVANCED_GENERATOR_OUTPUT = builder.defineInRange(
                "advanced_generator_output",
                GeneratorDefaults.advancedGeneratorOutput(GeneratorDefaults.ADVANCED_GENERATOR_MULTIPLIER),
                1D,
                100_000D
            );
            ADVANCED_GEOTHERMAL_GENERATOR_TANK_SIZE = builder.defineInRange(
                "advanced_geothermal_generator_tank_size", 24_000, 1_000, 1_000_000
            );
            ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY = builder.defineInRange(
                "advanced_geothermal_generator_capacity", 7_200D, 1D, 100_000D
            );
            ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT = builder.defineInRange(
                "advanced_geothermal_generator_output", GeneratorDefaults.ADVANCED_GEOTHERMAL_OUTPUT, 1D, 100_000D
            );
            builder.pop();
        }
    }

    private ICEGConfig() {
    }
}
