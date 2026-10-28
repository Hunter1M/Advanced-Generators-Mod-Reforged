package net.bdew.generators.recipes

import com.google.gson.JsonObject
import net.bdew.generators.registries.Recipes
import net.bdew.lib.multiblock.block.BlockModule
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.lib.recipes.{BaseMachineRecipe, BaseMachineRecipeSerializer}
import net.minecraft.network.{FriendlyByteBuf, RegistryFriendlyByteBuf}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.{RecipeSerializer, RecipeType}
import net.minecraft.world.level.block.Block
import net.minecraft.core.registries.BuiltInRegistries

class UpgradeRecipeSerializer extends BaseMachineRecipeSerializer[UpgradeRecipe] {
  override def fromJson(recipeId: ResourceLocation, obj: JsonObject): UpgradeRecipe = {
    val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(obj.get("item").getAsString))
    val from = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(obj.get("from").getAsString)).asInstanceOf[BlockModule[_ <: TileModule]]
    val to = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(obj.get("to").getAsString)).asInstanceOf[BlockModule[_ <: TileModule]]
    new UpgradeRecipe(recipeId, item, from, to)
  }

  override def fromNetwork(recipeId: ResourceLocation, buff: FriendlyByteBuf): UpgradeRecipe = {
    val item = BuiltInRegistries.ITEM.get(buff.readResourceLocation())
    val from = BuiltInRegistries.BLOCK.get(buff.readResourceLocation()).asInstanceOf[BlockModule[_ <: TileModule]]
    val to = BuiltInRegistries.BLOCK.get(buff.readResourceLocation()).asInstanceOf[BlockModule[_ <: TileModule]]
    new UpgradeRecipe(recipeId, item, from, to)
  }

  override def toNetwork(buffer: FriendlyByteBuf, recipe: UpgradeRecipe): Unit = {
    buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.item))
    buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(recipe.from))
    buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(recipe.to))
  }
}

class UpgradeRecipe(id: ResourceLocation, val item: Item, val from: BlockModule[_ <: TileModule], val to: BlockModule[_ <: TileModule]) extends BaseMachineRecipe(id) {
  override def getSerializer: RecipeSerializer[_] = Recipes.upgrade.serializer
  override def getType: RecipeType[_] = Recipes.upgrade.recipeType
}
