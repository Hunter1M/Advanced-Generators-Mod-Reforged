package net.bdew.generators.integration.ic2c

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class IC2Config(spec: ModConfigSpec.Builder) extends ConfigSection {
  val ratio: () => Double = getter(spec.comment("EU per FE Ratio")
    .defineInRange("Ratio", 0.25, 0D, 10D), Double.unbox)
}
