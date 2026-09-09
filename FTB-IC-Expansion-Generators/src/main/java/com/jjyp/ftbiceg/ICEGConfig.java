package com.jjyp.ftbiceg;

import com.nstut.ftbice.shared.GeneratorDefaults;
import dev.ftb.mods.ftblibrary.snbt.config.DoubleValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;

public class ICEGConfig {
    public static final SNBTConfig CONFIG = SNBTConfig.create("ftbiceg").comment(new String[]{"FTB IC Expansion: Generators config file"});
    public static final Machines MACHINES = new Machines();

    public static void init() {
    }

    public static final class Machines {
        private static final SNBTConfig GROUP = CONFIG.getGroup("Machines");
        public final DoubleValue ADVANCED_GENERATOR_CAPACITY;
        public final DoubleValue ADVANCED_GENERATOR_OUTPUT;
        public final DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY;
        public final DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT;

        public Machines() {
            ADVANCED_GENERATOR_CAPACITY = GROUP.getDouble("Advanced generator capacity", 12000.0, 1.0, 100000.0)
                .comment(new String[]{"Amount of energy stored in the Advanced Generator"});
            ADVANCED_GENERATOR_OUTPUT = GROUP.getDouble("Advanced generator output",
                    GeneratorDefaults.advancedGeneratorOutput(GeneratorDefaults.ADVANCED_GENERATOR_MULTIPLIER), 1.0, 100000.0)
                .comment(new String[]{"Energy created by the Advanced Generator"});
            ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY = GROUP.getDouble("Advanced geothermal generator capacity", 7200.0, 1.0, 100000.0)
                .comment(new String[]{"Amount of energy stored in the Advanced Geothermal Generator"});
            ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT = GROUP.getDouble("Advanced geothermal generator output",
                    GeneratorDefaults.ADVANCED_GEOTHERMAL_OUTPUT, 1.0, 100000.0)
                .comment(new String[]{"Energy created by the Advanced Geothermal Generator"});
        }
    }
}
