# Disabled optional integrations

These sources are part of the original Advanced Generators code but are not
compiled in the 1.21.1 NeoForge port yet:

- `net/bdew/generators/jei` — JEI categories written against the 1.20.1 JEI API.
- `net/bdew/generators/integration/mekanism` — Mekanism gas input integration
  written against the 1.20.1 Forge capability API.

The core mod builds and runs without these. IC2 support is enabled in the main
source tree against the IC2: Nostalgia API. To re-enable one of the disabled
integrations, move its source tree back under `src/main/scala`, restore the
registration calls in `registries/Machines.scala` (and the JEI plugin
registration if applicable), and port the code to the corresponding 1.21.1 API.
