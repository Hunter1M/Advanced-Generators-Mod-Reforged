package net.bdew.generators.integration.buildcraft

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class BuildCraftConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val mjPerFe: () => Double = doubleVal(spec, "MJPerFE",
    "BuildCraft MJ produced per FE (BuildCraft's default is 0.1)", 0.1D, 0.0001D, 1000D)
}
