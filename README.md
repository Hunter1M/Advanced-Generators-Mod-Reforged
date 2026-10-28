[![](http://cf.way2muchnoise.eu/full_advanced-generators_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/advanced-generators)
[![](http://cf.way2muchnoise.eu/versions/advanced-generators.svg)](https://www.curseforge.com/minecraft/mc-mods/advanced-generators)

# Advanced Generators — NeoForge 1.21.1 port

Free Form, Multi Block, Modular Power Generators.

This branch is a port of Advanced Generators to **Minecraft 1.21.1 / NeoForge 21.1.259+**.

## Requirements

- Java 21
- NeoForge **21.1.259** or newer for Minecraft 1.21.1

## Notes about this port

- **bdlib is embedded.** There is no bdlib release for 1.21.1 NeoForge, so the
  bdlib sources have been merged into this mod (`net.bdew.lib`). The separate
  `bdlib` and `ScalableCatsForce` dependencies are no longer required.
- The **Scala 2.13 runtime is bundled with Jar-in-Jar**, so no separate Scala
  loader mod is needed.
- **Energy outputs:**
  - FE / RF through the NeoForge Energy capability (`fe_output`)
  - IC2: Nostalgia EU (`eu_output_lv` … `eu_output_iv`)
  - Mekanism Joules (`joules_output`)
  - Electrodynamics Joules (`electrodynamics_output`)
  - BuildCraft Community Edition MJ (`mj_output`)
  - Create rotation / stress units through the standalone `kinetic_converter`
    block (FE → rotation)
- **Fuel configuration.** The `[Fuels]` section in
  `config/advgenerators-common.toml` lets you add fuels from any mod and tune
  existing values:
  - `Multiplier` scales every fuel value.
  - `FuelValues` accepts `"fluid_id=fe_per_mb"` and `"#fluid_tag=fe_per_mb"`
    entries, for example `"minecraft:lava=200"` or
    `"#c:fuel_light=3000"`.
  - Config entries override matching datapack fuel recipes.
- **Optional integrations that are disabled:** JEI category integration and
  Mekanism gas input. Their sources are kept in `port-disabled/`. Everything
  else works without them.

## Building

```bash
./gradlew build
```

The jar is written to `build/libs/`.

## Development runs

```bash
./gradlew runClient
./gradlew runServer
./gradlew runData
```

The `runData` task writes generated blockstates and loot tables to
`src/generated/resources`, which is included in the build.

## Original project

- CurseForge: https://www.curseforge.com/minecraft/mc-mods/advanced-generators
- Upstream: https://github.com/bdew-minecraft/generators
