# Advanced Generators
### Free-form multiblock power generation for Minecraft 1.21.1 - NeoForge

Advanced Generators is a modular, free-form multiblock power mod. Instead of building a fixed controller-and-casing structure, you build the machine itself out of functional modules. Add turbines, fuel tanks, capacitors, valves, sensors, and upgrades in any contiguous arrangement, and the controller turns the assembly into a working power plant. Expand it, shrink it, or reconfigure it at any time - no fixed blueprint, and no separate casing block.

This is the **Minecraft 1.21.1 / NeoForge 21.1.259+** port of bdew's Advanced Generators. **bdlib and the Scala runtime are bundled**, so there are no separate library or Scala-loader dependencies.

---

## Four machines, many ways to build them

### Gas Turbine
Burn liquid fuels directly for FE.

- Output scales with the number and tier of **Turbine** modules: **100 / 150 / 250 / 500 / 1,000 FE/t** per module.
- Default fuel efficiency is **110%**, rising to **135%** with a **Fuel/Air Mixer** and **185%** with a **Gas Mix Compressor**.
- **Fuel Tank** modules add **16,000 mB** of fuel storage each; the controller has a 1,000 mB internal buffer.
- **Power Capacitor** modules add **1M / 5M / 25M FE** of buffering.
- Six power-output channels by default, with per-face output configuration and redstone modes.

### Steam Turbine
Generate FE from steam with a simulated rotor.

- Accepts the **`c:steam`** fluid tag, including the mod's own steam, **IC2: Nostalgia steam**, and **superheated steam**.
- Turbines spin up over time, carry inertia, and lose speed under load instead of instantly producing maximum power.
- Default maximum speed is **5,000 RPM**.
- Default energy density is **2 FE/mB** of steam.
- Uses the same five Turbine module tiers as the Gas Turbine.

### Syngas Producer
Turn water and carbon into a portable, high-value fuel.

- **Heating Chamber** modules heat water into steam internally.
- **Mixing Chamber** modules combine that steam with carbon to produce **Syngas**.
- Carbon can come from coal, charcoal, coke, logs, planks, dusts, and blocks - different inputs have different carbon values.
- Syngas burns at **1,000 FE/mB** in a Gas Turbine, and can be stored in tanks or carried in a **Syngas Bucket**.
- Item Input and Fluid Output ports make the whole process automatable.

### Heat Exchanger
Move heat between fluids, or use heat to process materials.

- Default recipes turn **water into steam** and cool **lava into obsidian**.
- Use it to produce steam for a Steam Turbine, or as a fluid-processing machine.
- Heat Exchanger modules determine maximum heat transfer; internal heat builds up and decays over time.
- Supports item inputs/outputs and selectable fluid outputs.

---

## Energy outputs

Every energy system has its own output block or module. Install only the ones you need.

- **FE / RF** - Flux Generator
- **IC2: Nostalgia EU** - Low, Medium, High, Extreme, and Insane Voltage Emitters
- **Mekanism Joules** - Joules Emitter
- **BuildCraft Community Edition MJ** - MJ Emitter
- **Electrodynamics Joules** - Electrodynamics Joules Emitter
- **Create rotation / SU** - Kinetic Converter (standalone block)

Conversion ratios, output rates, and voltage behavior are configurable. IC2, Mekanism, BuildCraft, Electrodynamics, and Create integrations only load when those mods are present; the base mod works without them.

### Create Kinetic Converter

The Kinetic Converter turns FE into Create rotation.

- Provides configurable **RPM**, **stress capacity (SU)**, and **FE/t** - defaults are 256 RPM, 256 SU, and 100 FE/t.
- Only the two faces along its working axis expose shaft connections; the other four sides are solid casing.
- Use a **Create wrench** to cycle the working axis between **Y, Z, and X**. The new axis takes effect immediately, without save-and-reload.
- If FE runs out, the converter keeps its kinetic network attached but drops stress capacity to zero, so connected Create machines stop cleanly instead of the network being torn apart.

---

## Automation and control

- **Redstone Control Module** - enable on signal or disable on signal, with machine-specific actions such as fuel use, steam use, energy generation, heat exchange, water heating, and syngas mixing.
- **Sensor Module** - emits redstone based on tank fill levels, internal heat, turbine speed, power generation, and other machine conditions. Thresholds are configurable.
- **Item Input Port / Item Output Port** - automate carbon inputs and item byproducts.
- **Fluid Intake Valve / Fluid Output Valve** - pipe fuels, water, steam, syngas, and hot/cold fluids in and out.
- **Per-face output configuration** - choose which faces output and set independent redstone modes.
- **Controller GUI** - live production, fuel/steam consumption, efficiency, RPM, internal heat, tank levels, and output configuration. Buffers can be cleared from the GUI.

---

## Upgrades

Upgrades are installed in place with an upgrade kit - no need to break the machine.

- **Turbine modules** - Basic, Enhanced, Advanced, Reinforced, and Composite tiers.
- **Power Capacitors** - Basic, Advanced, and High Density tiers.
- **Fuel/Air Mixer** - raises gas-turbine fuel efficiency to 135%.
- **Gas Mix Compressor** - raises it to 185%; requires a Fuel/Air Mixer.
- **Turbine Upgrade Kits** - upgrade turbine modules to tiers 2-5.
- **Capacitor Upgrade Kits** - upgrade capacitor modules to tiers 2-3.

Recipes adapt to available metals, including steel, invar, electrum, signalum, enderium, and IC2 materials when those mods are installed.

---

## Fuel support

Advanced Generators ships with **73 built-in fuel recipes**, loaded as datapack recipes with per-mod conditions. Supported fluids include:

- **Advanced Generators** - Syngas
- **BuildCraft Community Edition** - oil, heavy oil, dense oil, distilled oil, oil residue, and the full range of gaseous, light, dense, mixed-light, mixed-heavy, hot, and searing fuels (20 entries)
- **IC2: Nostalgia** - biogas, biomass, creosote, hydrogen
- **Mekanism** - ethene, hydrogen
- **Electrodynamics** - ethanol, hydrogen, polyethylene
- **Thermal** - refined fuel
- **EnderIO** - hootch, fire water, rocket fuel
- **Forestry** - biomass, ethanol, seed oil, short mead
- **Immersive Engineering** - biodiesel, high-power biodiesel, ethanol, creosote, plant oil
- **Immersive Petroleum** - crude oil, diesel, sulfur diesel, gasoline, kerosene, naphtha, napalm, benzol, petroleum gas
- **TFMG** - butane, creosote, crude oil, diesel, ethylene, furnace gas, gasoline, heavy oil, hydrogen, kerosene, liquid asphalt, LPG, lubrication oil, napalm, naphtha, propane, propylene
- **Ad Astra** - cryo fuel, fuel, hydrogen, oil

Fuel values range from **80 FE/mB** for hydrogen to **11,260 FE/mB** for ethene, so fuel choice and processing chains matter.

### Add your own fuels

Any fluid or fluid tag can be added through the **`[Fuels]`** section of `config/advgenerators-common.toml`:

```toml
[Fuels]
    # Global multiplier applied to every fuel value
    Multiplier = 1.0
    # "fluid_id=fe_per_mb" or "#fluid_tag=fe_per_mb"
    FuelValues = [
        "minecraft:lava=200",
        "#c:fuel_light=3000"
    ]
```

Config entries override matching datapack recipes. Fuel recipes can also be added as datapack JSON using the `advgenerators:liquid_fuel` recipe type.

---

## Items and blocks

### Controllers
- **Gas Turbine Controller** - liquid-fuel power generation
- **Steam Turbine Controller** - steam power generation with rotor inertia
- **Syngas Producer Controller** - water + carbon -> syngas
- **Heat Exchanger Controller** - heat transfer and fluid processing

### Multiblock modules
- **Turbines** - Basic, Enhanced, Advanced, Reinforced, Composite
- **Storage** - Fuel Tank; Basic, Advanced, and High Density Power Capacitor
- **I/O** - Fluid Intake Valve, Fluid Output Valve, Item Input Port, Item Output Port
- **Machine parts** - Heating Chamber, Mixing Chamber, Heat Exchanger
- **Control** - Redstone Control Module, Sensor Module
- **Efficiency** - Fuel/Air Mixer, Gas Mix Compressor
- **Power outputs** - Flux Generator; Low/Medium/High/Extreme/Insane Voltage Emitters; Joules Emitter; MJ Emitter; Electrodynamics Joules Emitter; Kinetic Converter

### Crafting components and upgrades
- Iron Frame, Iron Tubing, Redstone-Iron Wiring, Control Circuit, Power IO Module, Pressure Valve, Advanced Pressure Valve, Turbine Blade
- Basic, Enhanced, Advanced, Reinforced, and Composite Turbine Rotors
- Upgrade Kit
- Turbine Upgrade Kits (tiers 2-5)
- Capacitor Upgrade Kits (tiers 2-3)
- Steam Bucket, Syngas Bucket

---

## Getting started

1. Craft the iron components: **Iron Frame**, **Iron Tubing**, **Redstone-Iron Wiring**, and a **Control Circuit**.
2. Craft a controller and a set of modules.
3. Place the controller. Attach modules directly adjacent to it - the controller adopts modules placed before or after it.
4. Build a working machine:
   - **Gas Turbine:** at least one Turbine module, a way to feed fuel (a Fluid Intake Valve is recommended), and a power output.
   - **Steam Turbine:** at least one Turbine module, a steam input, and a power output.
   - **Syngas Producer:** at least one Mixing Chamber, water input, carbon input, and a syngas output.
   - **Heat Exchanger:** at least one Heat Exchanger module, plus fluid/item inputs and outputs.
5. Right-click the controller to open its GUI, configure outputs, and connect redstone.

There is no required rectangular shape or casing. Any contiguous group of modules connected to the controller is a valid machine, provided its required modules are present and the per-module limits in the config are respected.

---

## Configuration

The common config file is created at `config/advgenerators-common.toml`. It includes sections for:

- `[TurbineFuel]` - turbine output, fuel capacity, energy capacity, and efficiency
- `[TurbineSteam]` - RPM, inertia, drag, steam energy density, and internal capacity
- `[SyngasProducer]` - heat, carbon/steam ratios, and chamber throughput
- `[HeatExchanger]` - heat capacity, transfer, and decay
- `[Modules]` - turbine, capacitor, fuel tank, and heat exchanger module stats
- `[Fuels]` - global fuel multiplier and custom/overriding fuel values
- `[Integration]` - EU, Joules, MJ, Electrodynamics, and Create conversion settings

Machine structure limits (how many of each module can connect) are configured per controller under the machine's own config section.

---

## Requirements and compatibility

- **Minecraft:** 1.21.1
- **NeoForge:** 21.1.259 or newer
- **Java:** 21
- **No hard dependencies.** bdlib and Scala are bundled with the mod.

Optional integrations:

- **IC2: Nostalgia** - EU output and steam compatibility
- **Mekanism** - Joules output
- **BuildCraft Community Edition** - MJ output and fuel support
- **Create** - FE-to-rotation Kinetic Converter
- **Electrodynamics** - Joules output

The base machines and FE output work without any of these installed.

---

## Credits

Original **Advanced Generators** by **bdew**, licensed under the MIT License.

Minecraft 1.21.1 / NeoForge port maintained by the Advanced Generators Reforged project. bdlib is embedded with the mod.

You are welcome to include Advanced Generators in modpacks.
