package net.bdew.generators.config

import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class FuelsConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val multiplier: () => Float = floatVal(spec, "Multiplier",
    "Global multiplier applied to all fuel values (both datapack recipes and FuelValues)",
    1.0F, 0.0F, 1000.0F)

  val values: () => java.util.List[_ <: String] = getter(
    spec.comment(
      "Additional or overriding fuel values, in FE per mB.",
      "Format: \"fluid_id=fe_per_mb\" or \"#fluid_tag=fe_per_mb\".",
      "Examples: \"minecraft:lava=200\", \"#c:fuel_light=3000\".",
      "The first matching entry wins; entries override datapack fuel recipes."
    ).defineListAllowEmpty[String]("FuelValues", new java.util.ArrayList[String](), (v: AnyRef) => v.isInstanceOf[String])
  )
}
