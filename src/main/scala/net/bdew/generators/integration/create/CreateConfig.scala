package net.bdew.generators.integration.create

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class CreateConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val fePerTick: () => Int = intVal(spec, "FEPerTick",
    "FE consumed per tick while providing rotation", 100, 1, 1000000)
  val speed: () => Int = intVal(spec, "SpeedRPM",
    "Generated rotation speed in RPM", 256, 1, 256)
  val stressCapacity: () => Int = intVal(spec, "StressCapacity",
    "Stress capacity (SU) provided while powered", 256, 1, 1000000)
  val buffer: () => Int = intVal(spec, "Buffer",
    "Internal FE buffer", 10000, 1, 100000000)
}
