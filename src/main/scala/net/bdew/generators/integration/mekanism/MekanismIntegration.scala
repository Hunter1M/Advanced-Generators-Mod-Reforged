package net.bdew.generators.integration.mekanism

import net.bdew.generators.integration.mekanism.joulesOutput.{BlockItemJoulesOutput, BlockJoulesOutput, TileJoulesOutput}
import net.bdew.generators.registries.Machines
import net.neoforged.fml.ModList
import org.apache.logging.log4j.{LogManager, Logger}

object MekanismIntegration {
  val log: Logger = LogManager.getLogger

  lazy val isAvailable: Boolean = {
    if (ModList.get().isLoaded("mekanism")) {
      try {
        Class.forName("mekanism.api.energy.IStrictEnergyHandler")
        log.info("Mekanism loaded, activating Joules integration")
        true
      } catch {
        case err: Throwable =>
          log.warn("Mekanism loaded, but failed to access its energy API", err)
          false
      }
    } else {
      log.info("Mekanism not loaded, skipping Joules integration")
      false
    }
  }

  def registerMachines(): Unit = {
    Machines.registerModule("joules_output",
      () => new BlockJoulesOutput,
      new TileJoulesOutput(_, _, _),
      new BlockItemJoulesOutput(_))
  }
}
