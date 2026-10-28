package net.bdew.generators.recipes

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.bdew.generators.registries.Recipes
import net.bdew.lib.recipes.{BaseMachineRecipe, BaseMachineRecipeSerializer}
import net.minecraft.network.{FriendlyByteBuf, RegistryFriendlyByteBuf}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.{Ingredient, RecipeSerializer, RecipeType}

class CarbonSourceRecipeSerializer extends BaseMachineRecipeSerializer[CarbonSourceRecipe] {
  override def fromJson(recipeId: ResourceLocation, obj: JsonObject): CarbonSourceRecipe = {
    val ingr = Ingredient.CODEC.parse(JsonOps.INSTANCE, obj.get("items")).getOrThrow()
    val carbonValue = obj.get("carbonValue").getAsFloat
    new CarbonSourceRecipe(recipeId, ingr, carbonValue)
  }

  override def fromNetwork(recipeId: ResourceLocation, buff: FriendlyByteBuf): CarbonSourceRecipe = {
    val ingr = Ingredient.CONTENTS_STREAM_CODEC.decode(buff.asInstanceOf[RegistryFriendlyByteBuf])
    val carbonValue = buff.readFloat()
    new CarbonSourceRecipe(recipeId, ingr, carbonValue)
  }

  override def toNetwork(buffer: FriendlyByteBuf, recipe: CarbonSourceRecipe): Unit = {
    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer.asInstanceOf[RegistryFriendlyByteBuf], recipe.ingredient)
    buffer.writeFloat(recipe.carbonValue)
  }
}

class CarbonSourceRecipe(id: ResourceLocation, val ingredient: Ingredient, val carbonValue: Float) extends BaseMachineRecipe(id) {
  override def getSerializer: RecipeSerializer[_] = Recipes.carbonSource.serializer
  override def getType: RecipeType[_] = Recipes.carbonSource.recipeType
}
