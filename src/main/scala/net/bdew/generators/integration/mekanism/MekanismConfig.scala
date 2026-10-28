package net.bdew.generators.integration.mekanism

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class MekanismConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val fePerJoule: () => Double = doubleVal(spec, "FEPerJoule",
    "FE required for one Joule of Mekanism energy", 2.5D, 0.0001D, 10000D)
}
