package net.bdew.generators.config

import net.bdew.generators.Generators
import net.bdew.generators.controllers.exchanger.ConfigExchanger
import net.bdew.generators.controllers.steam.ConfigSteamTurbine
import net.bdew.generators.controllers.syngas.ConfigSyngas
import net.bdew.generators.controllers.turbine.ConfigFuelTurbine
import net.bdew.generators.integration.IntegrationConfig
import net.bdew.lib.BdLib
import net.bdew.lib.config.ConfigSection
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent

object Config {
  private val commonBuilder = new ModConfigSpec.Builder

  val FuelTurbine: ConfigFuelTurbine = ConfigSection(commonBuilder, "TurbineFuel", new ConfigFuelTurbine(commonBuilder))
  val SteamTurbine: ConfigSteamTurbine = ConfigSection(commonBuilder, "TurbineSteam", new ConfigSteamTurbine(commonBuilder))
  val SyngasProducer: ConfigSyngas = ConfigSection(commonBuilder, "SyngasProducer", new ConfigSyngas(commonBuilder))
  val HeatExchanger: ConfigExchanger = ConfigSection(commonBuilder, "HeatExchanger", new ConfigExchanger(commonBuilder))
  val Modules: ModulesConfig = ConfigSection(commonBuilder, "Modules", new ModulesConfig(commonBuilder))
  val Fuels: FuelsConfig = ConfigSection(commonBuilder, "Fuels", new FuelsConfig(commonBuilder))

  val Integration: IntegrationConfig = ConfigSection(commonBuilder, "Integration", new IntegrationConfig(commonBuilder))

  val COMMON: ModConfigSpec = commonBuilder.build()

  def init(container: ModContainer): Unit = {
    container.registerConfig(ModConfig.Type.COMMON, Config.COMMON)
    CarbonValueRegistry.init()
    TurbineFuelRegistry.init()
    ExchangerRecipeRegistry.init()

    BdLib.modBus.addListener((event: ModConfigEvent.Loading) => onConfigEvent(event))
    BdLib.modBus.addListener((event: ModConfigEvent.Reloading) => onConfigEvent(event))
  }

  private def onConfigEvent(event: ModConfigEvent): Unit = {
    if (event.getConfig.getModId == Generators.ModId && event.getConfig.getType == ModConfig.Type.COMMON)
      TurbineFuelRegistry.refreshConfig()
  }
}
