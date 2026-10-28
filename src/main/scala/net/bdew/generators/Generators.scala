package net.bdew.generators

import net.bdew.generators.config.Config
import net.bdew.generators.datagen.DataGeneration
import net.bdew.generators.integration.create.CreateIntegration
import net.bdew.generators.network.NetworkHandler
import net.bdew.generators.registries._
import net.bdew.lib.BdLib
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge

object Generators {
  final val ModId = "advgenerators"

  def rl(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(ModId, path)

  var modContainer: ModContainer = _

  def init(modBus: IEventBus, container: ModContainer): Unit = {
    modContainer = container
    BdLib.init(modBus)

    Config.init(container)
    Items.init()
    Blocks.init()
    Fluids.init()
    Machines.init()
    if (CreateIntegration.isAvailable) {
      CreateIntegration.registerBlocks()
      BdLib.modBus.addListener((event: net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent) =>
        CreateIntegration.registerCapabilities(event))
    }
    Recipes.init()
    Containers.init()
    NetworkHandler.init()

    modBus.addListener((event: net.neoforged.neoforge.data.event.GatherDataEvent) => DataGeneration.onGatherData(event))

    if (FMLEnvironment.dist.isClient) {
      ClientHandler.init(modBus)
    }
  }
}
