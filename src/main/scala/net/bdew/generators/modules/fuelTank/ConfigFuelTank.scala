package net.bdew.generators.modules.fuelTank

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class ConfigFuelTank(spec: ModConfigSpec.Builder, defaultCapacity: Int) extends ConfigSection {
  val capacity: () => Int = intVal(spec, "Capacity", "Capacity (mB)", defaultCapacity)
}
