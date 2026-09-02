# FTB IC Expansion

Modern multi-version workspace for the legacy **FTB IC Expansion Core** and **FTB IC Expansion Generators** mods.

## Supported development targets

| Target | Loader | Java | Status |
| --- | --- | ---: | --- |
| Minecraft 1.19.2 | Forge 43.2.8 | 17 | Legacy implementation / behavioral baseline |
| Minecraft 26.1.2 | NeoForge 26.1.2.76 | 25 | Port workspace; loader shells compile while FTBIC-specific machine adapters are migrated |

The repository originally targeted Minecraft **1.19.2**, not 1.18.2. The 26.1.2 lane follows the current NeoForge/ModDevGradle conventions used by the other UpperMoon0 multi-version mods and compiles against the verified FTB Industrial Contraptions 26.1.2 release line.

## Layout

- `common/` — version-neutral identifiers, rules and tests. Keep Minecraft/loader/FTBIC internals out of this module.
- `FTB-IC-Expansion-Core/` — original Forge 1.19.2 Core implementation.
- `FTB-IC-Expansion-Generators/` — original Forge 1.19.2 Generators implementation.
- `legacy-1.19.2/` — Gradle 7.5.1 workspace that builds the two legacy projects together.
- `neoforge-26.1.2-core/` — Core's NeoForge 26.1.2 adapter.
- `neoforge-26.1.2-generators/` — Generators' NeoForge 26.1.2 adapter.
- `gradle/neoforge-26.1.2.gradle` — shared 26.1.2 ModDevGradle convention.

## Build

```bash
./gradlew testAllVersions
```

The root build runs shared unit tests, the isolated Gradle 7.5.1 Forge build for 1.19.2, and both NeoForge 26.1.2 modules. CI uses Java 17 for the Gradle process and toolchain provisioning for Java 25.

## Porting rule

Share stable behavior and data, not incompatible implementation details. The old Core reconstructs several FTBIC internals, and the 26.1.2 FTBIC API/registry model has materially changed. Minecraft/Forge/NeoForge and FTBIC adapter code therefore stays version-specific while constants, calculations and other loader-neutral behavior live in `common`.

## Publishing

Version numbers live in root `gradle.properties`. Automated release/publishing is intentionally gated for 26.1.2 with `publish_26_1_2=false` until feature parity is reached; CI still builds the target so API/toolchain drift is caught immediately.
