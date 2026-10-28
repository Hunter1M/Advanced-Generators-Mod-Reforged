package net.bdew.lib.recipes

import net.bdew.lib.{BdLib, Client, Event, Event2}
import net.minecraft.core.RegistryAccess
import net.minecraft.server.packs.resources.{ResourceManager, SimplePreparableReloadListener}
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.crafting.RecipeManager
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent
import net.neoforged.neoforge.event.{AddReloadListenerEvent, TagsUpdatedEvent}

object RecipeReloadListener {
  var serverRecipeManager: RecipeManager = _
  var serverRegistryAccess: RegistryAccess = _

  var clientRecipeManager: RecipeManager = _

  val onClientRecipeUpdate: Event2[RecipeManager, RegistryAccess] = Event[RecipeManager, RegistryAccess]
  val onServerRecipeUpdate: Event2[RecipeManager, RegistryAccess] = Event[RecipeManager, RegistryAccess]

  def addReloadListener(event: AddReloadListenerEvent): Unit = {
    event.addListener(
      new SimplePreparableReloadListener[Void]() {
        override protected def prepare(manager: ResourceManager, profiler: ProfilerFiller): Void = {
          serverRecipeManager = event.getServerResources.getRecipeManager
          serverRegistryAccess = event.getRegistryAccess
          null
        }

        override protected def apply(nothing: Void, resourceManagerIn: ResourceManager, profilerIn: ProfilerFiller): Unit = {
        }
      })
  }

  def tagsUpdated(event: TagsUpdatedEvent): Unit = {
    if (serverRecipeManager != null)
      onServerRecipeUpdate.trigger(serverRecipeManager, serverRegistryAccess)
  }

  def recipesUpdated(event: RecipesUpdatedEvent): Unit = {
    clientRecipeManager = event.getRecipeManager
    onClientRecipeUpdate.trigger(clientRecipeManager, Client.minecraft.level.registryAccess())
  }
}
