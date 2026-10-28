package net.bdew.generators.integration.buildcraft

import net.bdew.generators.integration.buildcraft.mjOutput.{BlockItemMjOutput, BlockMjOutput, TileMjOutput}
import net.bdew.generators.registries.Machines
import net.neoforged.fml.ModList
import org.apache.logging.log4j.{LogManager, Logger}

object BuildCraftIntegration {
  val log: Logger = LogManager.getLogger

  lazy val isAvailable: Boolean = {
    if (ModList.get().isLoaded("buildcraftenergy")) {
      try {
        Class.forName("buildcraft.lib.internal.mj.MjCapabilities")
        log.info("BuildCraft Energy loaded, activating MJ integration")
        true
      } catch {
        case err: Throwable =>
          log.warn("BuildCraft Energy loaded, but failed to access its MJ API", err)
          false
      }
    } else {
      log.info("BuildCraft Energy not loaded, skipping MJ integration")
      false
    }
  }

  def registerMachines(): Unit = {
    Machines.registerModule("mj_output",
      () => new BlockMjOutput,
      new TileMjOutput(_, _, _),
      new BlockItemMjOutput(_))
  }
}
