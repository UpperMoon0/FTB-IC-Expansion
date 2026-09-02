package com.jjyp.ftbiceg;

import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EphemeralTestServerProvider.class)
class RuntimeSmokeTest {
    @Test
    void requiredModsBootTogether(MinecraftServer server) {
        assertNotNull(server);
        ModList mods = ModList.get();
        assertTrue(mods.isLoaded("ftbic"), "FTB Industrial Contraptions must be present at runtime");
        assertTrue(mods.isLoaded("ftbicec"), "FTB IC Expansion Core must be present at runtime");
        assertTrue(mods.isLoaded("ftbiceg"), "FTB IC Expansion Generators must be present at runtime");
    }
}
