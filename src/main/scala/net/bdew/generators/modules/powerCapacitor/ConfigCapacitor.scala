package net.bdew.generators.modules.powerCapacitor

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class ConfigCapacitor(spec: ModConfigSpec.Builder, defaultCapacity: Int) extends ConfigSection {
  val capacity: () => Int = getter(spec.comment("Capacity (FE)")
    .defineInRange("Capacity", defaultCapacity, 1, Int.MaxValue), Int.unbox)
}
