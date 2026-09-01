package com.jjyp.ftbiceg;

import dev.ftb.mods.ftblibrary.snbt.config.*;

public class ICEGConfig {
    public static SNBTConfig CONFIG = SNBTConfig.create("ftbiceg").comment(new String[]{"FTB IC Expansion: Generators config file"});
    public static final Machines MACHINES;
    public ICEGConfig() {
    }
    public static void init() {
    }

    static {
        MACHINES = new Machines();
    }
    public static final class Machines {
        private static final SNBTConfig MACHINES;
        public final DoubleValue ADVANCED_GENERATOR_CAPACITY;
        public final DoubleValue ADVANCED_GENERATOR_OUTPUT;
        public final DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY;
        public final DoubleValue ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT;
        public Machines() {
            this.ADVANCED_GENERATOR_CAPACITY = MACHINES.getDouble("Advanced generator capacity", 12000.0, 1.0, 100000.0).comment(new String[]{"Amount of energy is stored in the Advanced generator"});
            this.ADVANCED_GENERATOR_OUTPUT = MACHINES.getDouble("Advanced generator output", 30.0, 1.0, 100000.0).comment(new String[]{"Energy created from the Advanced generator"});
            this.ADVANCED_GEOTHERMAL_GENERATOR_CAPACITY = MACHINES.getDouble("Advanced geothermal generator capacity", 7200.0, 1.0, 100000.0).comment(new String[]{"Amount of energy is stored in the Advanced geothermal generator"});
            this.ADVANCED_GEOTHERMAL_GENERATOR_OUTPUT = MACHINES.getDouble("Advanced geothermal generator output", 60.0, 1.0, 100000.0).comment(new String[]{"Energy created from the Advanced geothermal generator"});
        }

        static {
            MACHINES = ICEGConfig.CONFIG.getGroup("Machines");
        }
    }
}
