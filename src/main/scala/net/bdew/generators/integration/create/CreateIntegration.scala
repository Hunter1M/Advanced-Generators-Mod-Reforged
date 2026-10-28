package net.bdew.generators.integration.create

import net.bdew.generators.registries.Blocks
import net.bdew.lib.registries.RegistryObject
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.energy.IEnergyStorage
import org.apache.logging.log4j.{LogManager, Logger}

object CreateIntegration {
  val log: Logger = LogManager.getLogger

  private var converterTe: RegistryObject[net.minecraft.world.level.block.entity.BlockEntityType[TileKineticConverter]] = _

  lazy val isAvailable: Boolean = {
    if (ModList.get().isLoaded("create")) {
      try {
        Class.forName("com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity")
        log.info("Create loaded, activating SU integration")
        true
      } catch {
        case err: Throwable =>
          log.warn("Create loaded, but failed to access its kinetic API", err)
          false
      }
    } else {
      log.info("Create not loaded, skipping SU integration")
      false
    }
  }

  def registerBlocks(): Unit = {
    val defn = Blocks.define("kinetic_converter", () => new BlockKineticConverter)
      .withTE(new TileKineticConverter(_, _, _))
      .withDefaultItem
      .register
    converterTe = defn.teType
  }

  def registerCapabilities(event: RegisterCapabilitiesEvent): Unit = {
    if (converterTe != null) {
      event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, converterTe.get,
        (be: TileKineticConverter, side: net.minecraft.core.Direction) => be.energyHandler.asInstanceOf[IEnergyStorage])
    }
  }
}
