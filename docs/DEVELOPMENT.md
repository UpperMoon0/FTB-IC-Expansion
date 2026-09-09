# Development

FTB IC Expansion is a multi-version repository with an isolated legacy Forge workspace and a modern NeoForge root workspace.

## Prerequisites

- Java 17 for the Forge 1.19.2 workspace.
- Java 25 for the NeoForge 26.1.2 build/runtime lane.
- Git and a normal Gradle-capable development environment.

The repository contains Gradle wrappers; a separate system Gradle installation is not required.

CurseForge dependencies use the pinned file IDs and filenames in
`gradle/curseforge-repositories.gradle`. Gradle downloads these JARs directly from
CurseForge's CDN and caches them normally, without depending on CurseMaven availability.
When updating a CurseForge dependency, update its entry in that file alongside the
dependency version/file ID. The existing `curse.maven` coordinates are retained for
ForgeGradle remapping; no dependency versions are changed by the repository mapping.

## Full verification

From the repository root:

```bash
./gradlew testAllVersions
```

CI also runs the version lanes independently so a failure clearly identifies the affected target.

## Forge 1.19.2

The legacy projects are built together through the isolated workspace:

```bash
./legacy-1.19.2/gradlew -p legacy-1.19.2 build
```

Use Java 17 for this lane. `FTB-IC-Expansion-Generators` consumes the local Core project so development does not accidentally compile against a stale released Core API.

## NeoForge 26.1.2

Use Java 25 and run:

```bash
./gradlew buildModern2612
```

The modern validation includes a lightweight Minecraft server bootstrap with FTB Industrial Contraptions, Core, and Generators loaded together.

## Development integrations

The development runtime includes the integrations needed to catch compatibility drift around the generators, including JEI, Jade, GuideME, and Patchouli where relevant. Production dependencies remain required or optional according to the mod metadata rather than simply mirroring the dev runtime.

## Release contract

Root `gradle.properties` is the single version source:

```properties
core_version=...
generators_version=...
```

When changing a release version, add the matching release notes:

```text
changelogs/core/v<core_version>.md
changelogs/generators/v<generators_version>.md
```

The release workflow validates the builds, uploads changed modules to CurseForge, and creates the corresponding GitHub release. The changelog files are used as the release notes on both surfaces.

## Documentation contract

Treat documentation as part of the feature, not a follow-up task. Player-visible behavior should remain synchronized across:

- `README.md` for repository/project overview;
- `curseforge/` for copy-paste-ready CurseForge project descriptions;
- `changelogs/` for release-specific changes;
- Patchouli entries for the 1.19.2 guide;
- GuideME pages for the 26.1.2 guide.

Architecture or build-only details belong under `docs/` rather than dominating the player-facing README.
