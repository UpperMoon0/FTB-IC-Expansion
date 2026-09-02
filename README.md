# FTB IC Expansion

Modern multi-version workspace for the legacy **FTB IC Expansion Core** and **FTB IC Expansion Generators** mods.

## Supported development targets

| Target | Loader | Java | Status |
| --- | --- | ---: | --- |
| Minecraft 1.19.2 | Forge 43.2.8 | 17 | Legacy implementation / behavioral baseline |
| Minecraft 26.1.2 | NeoForge 26.1.2.76 | 25 | Port workspace; loader shells compile and runtime-smoke while FTBIC-specific machine adapters are migrated |

The repository originally targeted Minecraft **1.19.2**, not 1.18.2. The modern lane follows current NeoForge/ModDevGradle conventions and compiles against FTB Industrial Contraptions 26.1.2.10.

## Layout

- `common/` — version-neutral identifiers, rules and tests. Keep Minecraft/loader/FTBIC internals out of this module.
- `FTB-IC-Expansion-Core/` — original Forge 1.19.2 Core implementation.
- `FTB-IC-Expansion-Generators/` — original Forge 1.19.2 Generators implementation.
- `legacy-1.19.2/` — Gradle 7.5.1 workspace that builds the two legacy projects together.
- `neoforge-26.1.2-core/` — Core's NeoForge 26.1.2 adapter.
- `neoforge-26.1.2-generators/` — Generators' NeoForge 26.1.2 adapter and runtime smoke test.
- `gradle/neoforge-26.1.2.gradle` — shared 26.1.2 ModDevGradle convention.

The root modern workspace uses Gradle 9.1.0. The isolated legacy workspace stays on Gradle 7.5.1 so ForgeGradle 5 is not forced onto an unsupported modern Gradle runtime.

## Build

For a local full verification from Java 17, run:

```bash
./gradlew testAllVersions
```

CI deliberately separates shared tests, Forge 1.19.2, and NeoForge 26.1.2 into independent jobs. The modern job runs on Java 25 and includes a lightweight ephemeral Minecraft server test that verifies FTBIC, Core, and Generators load together.

## Porting rule

Share stable behavior and data, not incompatible implementation details. The old Core reconstructs several FTBIC internals, and the 26.1.2 FTBIC API/registry model has materially changed. Minecraft/Forge/NeoForge and FTBIC adapter code therefore stays version-specific while constants, calculations and other loader-neutral behavior live in `common`.

Each shipped mod owns its registry lifecycle. The dormant `ftbiceop` registry fallback from the legacy Core was removed because no corresponding module exists in this repository; unknown expansion mod IDs now fail fast instead of silently registering into Core.

## Publishing

Version numbers live in root `gradle.properties`. CurseForge uploads include required-dependency relations. NeoForge 26.1.2 publishing remains gated by `publish_26_1_2=false` until machine feature parity is reached; CI still builds and boots the modern target so dependency/runtime drift is caught before publishing is enabled.
