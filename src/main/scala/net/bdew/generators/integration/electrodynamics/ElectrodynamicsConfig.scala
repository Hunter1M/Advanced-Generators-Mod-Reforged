package net.bdew.generators.integration.electrodynamics

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class ElectrodynamicsConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val joulesPerFe: () => Double = doubleVal(spec, "JoulesPerFE",
    "Electrodynamics Joules produced per FE", 1.0D, 0.0001D, 10000D)
  val voltage: () => Double = doubleVal(spec, "Voltage",
    "Preferred output voltage (clamped to the receiver's accepted range)", 120.0D, 1.0D, 1000000D)
}
