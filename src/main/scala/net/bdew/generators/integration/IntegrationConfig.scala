package net.bdew.generators.integration

import net.bdew.generators.integration.buildcraft.{BuildCraftConfig, BuildCraftIntegration}
import net.bdew.generators.integration.create.{CreateConfig, CreateIntegration}
import net.bdew.generators.integration.electrodynamics.{ElectrodynamicsConfig, ElectrodynamicsIntegration}
import net.bdew.generators.integration.ic2c.{IC2CIntegration, IC2Config}
import net.bdew.generators.integration.mekanism.{MekanismConfig, MekanismIntegration}
import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec

class IntegrationConfig(spec: ModConfigSpec.Builder) extends ConfigSection {
  val IC2: IC2Config =
    if (IC2CIntegration.isAvailable) ConfigSection(spec, "IC2C", new IC2Config(spec)) else null

  val Mekanism: MekanismConfig =
    if (MekanismIntegration.isAvailable) ConfigSection(spec, "Mekanism", new MekanismConfig(spec)) else null

  val BuildCraft: BuildCraftConfig =
    if (BuildCraftIntegration.isAvailable) ConfigSection(spec, "BuildCraft", new BuildCraftConfig(spec)) else null

  val Create: CreateConfig =
    if (CreateIntegration.isAvailable) ConfigSection(spec, "Create", new CreateConfig(spec)) else null

  val Electrodynamics: ElectrodynamicsConfig =
    if (ElectrodynamicsIntegration.isAvailable) ConfigSection(spec, "Electrodynamics", new ElectrodynamicsConfig(spec)) else null
}
