package com.jjyp.ftbicec;

import com.nstut.ftbice.shared.ExpansionIds;
import net.neoforged.fml.common.Mod;

@Mod(ExpansionIds.CORE)
public final class FTBICEC {
    public static final String MODID = ExpansionIds.CORE;

    public FTBICEC() {
        // 26.1.2 loader entrypoint. FTBIC-specific feature adapters are ported independently
        // from the version-neutral code so incompatible internals do not leak into common.
    }
}
