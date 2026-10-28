package net.bdew.generators.integration.electrodynamics

import net.bdew.generators.integration.electrodynamics.output.{BlockElectrodynamicsOutput, BlockItemElectrodynamicsOutput, TileElectrodynamicsOutput}
import net.bdew.generators.registries.Machines
import net.neoforged.fml.ModList
import org.apache.logging.log4j.{LogManager, Logger}

object ElectrodynamicsIntegration {
  val log: Logger = LogManager.getLogger

  lazy val isAvailable: Boolean = {
    if (ModList.get().isLoaded("electrodynamics")) {
      try {
        Class.forName("electrodynamics.prefab.utilities.ElectricityUtils")
        Class.forName("voltaic.registers.VoltaicCapabilities")
        log.info("Electrodynamics loaded, activating Joules integration")
        true
      } catch {
        case err: Throwable =>
          log.warn("Electrodynamics loaded, but failed to access its energy API", err)
          false
      }
    } else {
      log.info("Electrodynamics not loaded, skipping Joules integration")
      false
    }
  }

  def registerMachines(): Unit = {
    Machines.registerModule("electrodynamics_output",
      () => new BlockElectrodynamicsOutput,
      new TileElectrodynamicsOutput(_, _, _),
      new BlockItemElectrodynamicsOutput(_))
  }
}
