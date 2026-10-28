package net.bdew.lib.datagen

import net.minecraft.data.{DataGenerator, PackOutput}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ModelFile.{ExistingModelFile, UncheckedModelFile}
import net.neoforged.neoforge.client.model.generators.{ItemModelProvider, ModelFile}
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.minecraft.core.registries.BuiltInRegistries

abstract class ItemModelGenerator(gen: PackOutput, modId: String, efh: ExistingFileHelper) extends ItemModelProvider(gen, modId, efh) {
  def vanillaModel(name: String): ExistingModelFile = {
    new ExistingModelFile(ResourceLocation.parse(name), efh)
  }

  def uncheckedModel(name: String): ModelFile = {
    new UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(modId, name))
  }

  def simpleItemModel(item: Item, texture: String): Unit = {
    getBuilder(BuiltInRegistries.ITEM.getKey(item).getPath)
      .parent(vanillaModel("item/generated"))
      .texture("layer0", ResourceLocation.fromNamespaceAndPath(modId, texture))
  }
}
