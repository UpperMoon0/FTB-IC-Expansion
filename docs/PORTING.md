# Porting and architecture

FTB IC Expansion supports two materially different FTB Industrial Contraptions generations from one repository:

- Forge 1.19.2 / Java 17 — legacy implementation and behavioral baseline.
- NeoForge 26.1.2 / Java 25 — current implementation against FTB Industrial Contraptions 26.1.2.x.

The project deliberately shares **stable behavior and data**, not incompatible implementation details.

## Why the implementations stay version-specific

FTB Industrial Contraptions changed its registry, recipe, networking, capability, persistence, and machine APIs between the two supported generations. Trying to force both targets through one Minecraft-facing implementation would create a large compatibility abstraction that is harder to verify than the machines themselves.

The repository therefore keeps Minecraft/Forge/NeoForge and FTBIC adapter code in the target-specific modules while `common/` owns constants, calculations, and tests that can remain loader-neutral.

## Workspace layout

- `common/` — version-neutral rules and tests.
- `FTB-IC-Expansion-Core/` — original Forge 1.19.2 Core implementation.
- `FTB-IC-Expansion-Generators/` — original Forge 1.19.2 Generators implementation.
- `legacy-1.19.2/` — isolated Gradle 7.5.1 workspace used to build the legacy projects together.
- `neoforge-26.1.2-core/` — Core's modern adapter.
- `neoforge-26.1.2-generators/` — modern generator gameplay and integrations.
- `gradle/neoforge-26.1.2.gradle` — shared NeoForge 26.1.2 ModDevGradle conventions.

The root modern workspace uses Gradle 9.1.0. The legacy workspace remains isolated because ForgeGradle 5 belongs on its supported Gradle generation rather than being forced into the modern root build.

## FTBIC compatibility boundary

The legacy implementation follows the upstream 1.19 machine patterns where they remain safe and stable.

The modern implementation does not copy the entire legacy machine hierarchy. FTBIC's modern generator superclass is coupled to FTBIC's own registered machine instances, so expansion generators cannot cleanly subclass it as a supported addon API. Instead, IC Expansion owns its gameplay state and isolates the minimum upstream transport behavior it needs behind the Core compatibility bridge.

That bridge is responsible for FTBIC-specific Zap/cable transport and FE conversion. Fuel, fluid, battery, persistence, interaction, and expansion configuration remain owned by the expansion generator classes.

## Behavioral parity rules

When changing a generator:

1. Preserve player-visible behavior across both supported targets unless a target API requires a deliberate difference.
2. Use FTBIC recipe/config sources rather than hardcoded copies whenever upstream exposes the behavior as data.
3. Keep dangerous correctness fixes in both targets.
4. Leave safe legacy architecture in place when rewriting it would create more risk than value.
5. Prefer modern NeoForge capabilities and persistence APIs in the 26.1.2 implementation.

## Publishing

Modern publishing is controlled by `publish_26_1_2` in the root `gradle.properties`. When enabled, release automation builds and validates both targets before uploading the modern files.

The release workflow is version-driven: changing `core_version` or `generators_version` publishes the corresponding module. Every changed module must also have a matching source-controlled changelog under `changelogs/`.
