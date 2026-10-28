package net.bdew.generators

import net.bdew.generators.controllers.exchanger.{ContainerExchanger, GuiExchanger}
import net.bdew.generators.controllers.steam.{ContainerSteamTurbine, GuiSteamTurbine}
import net.bdew.generators.controllers.syngas.{ContainerSyngas, GuiSyngas}
import net.bdew.generators.controllers.turbine.{ContainerFuelTurbine, GuiFuelTurbine}
import net.bdew.generators.modules.control.{ContainerControl, GuiControl}
import net.bdew.generators.modules.sensor.{ContainerSensor, GuiSensor}
import net.bdew.generators.registries.{Blocks, Containers, Fluids}
import net.bdew.lib.multiblock.render.MultiblockRenderHelper
import net.minecraft.client.renderer.{ItemBlockRenderTypes, RenderType}
import net.bdew.lib.recipes.RecipeReloadListener
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.client.event.ModelEvent.RegisterGeometryLoaders
import net.neoforged.neoforge.client.event.{RegisterColorHandlersEvent, RegisterMenuScreensEvent}
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

object ClientHandler {
  def init(modBus: IEventBus): Unit = {
    modBus.addListener((ev: RegisterGeometryLoaders) => registerLoaders(ev))
    modBus.addListener((ev: RegisterColorHandlersEvent.Block) => registerBlockColors(ev))
    modBus.addListener((ev: RegisterMenuScreensEvent) => registerScreens(ev))
    modBus.addListener((ev: FMLClientSetupEvent) => clientSetup(ev))
    NeoForge.EVENT_BUS.addListener((ev: net.neoforged.neoforge.client.event.RecipesUpdatedEvent) => RecipeReloadListener.recipesUpdated(ev))
  }

  def registerLoaders(ev: RegisterGeometryLoaders): Unit = {
    ev.register(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(Generators.ModId, "multiblock_model"), CustomModels.multiblockModelLoader)
  }

  def registerBlockColors(ev: RegisterColorHandlersEvent.Block): Unit = {
    MultiblockRenderHelper.setupColors(ev, Blocks.all.map(_.get()), GeneratorsResourceProvider)
  }

  def registerScreens(ev: RegisterMenuScreensEvent): Unit = {
    ev.register(Containers.fuelTurbine.get(), (c: ContainerFuelTurbine, i, _) => new GuiFuelTurbine(c, i))
    ev.register(Containers.steamTurbine.get(), (c: ContainerSteamTurbine, i, _) => new GuiSteamTurbine(c, i))
    ev.register(Containers.syngas.get(), (c: ContainerSyngas, i, _) => new GuiSyngas(c, i))
    ev.register(Containers.exchanger.get(), (c: ContainerExchanger, i, _) => new GuiExchanger(c, i))
    ev.register(Containers.control.get(), (c: ContainerControl, i, _) => new GuiControl(c, i))
    ev.register(Containers.sensor.get(), (c: ContainerSensor, i, _) => new GuiSensor(c, i))
  }

  def clientSetup(ev: FMLClientSetupEvent): Unit = {
    ev.enqueueWork(new Runnable {
      override def run(): Unit = {
        ItemBlockRenderTypes.setRenderLayer(Fluids.syngas.source.get(), RenderType.TRANSLUCENT)
        ItemBlockRenderTypes.setRenderLayer(Fluids.syngas.flowing.get(), RenderType.TRANSLUCENT)
      }
    })
  }
}
