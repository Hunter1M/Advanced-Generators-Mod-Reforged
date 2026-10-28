package net.bdew.generators.modules.exchanger

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class ConfigExchanger(spec: ModConfigSpec.Builder, defaultHeatTransfer: Double) extends ConfigSection {
  val heatTransfer: () => Double = doubleVal(spec, "HeatTransfer", "Heat Units per tick", defaultHeatTransfer)
}
