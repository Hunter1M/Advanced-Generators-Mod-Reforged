package net.bdew.lib.recipes

import com.google.gson.JsonObject
import com.mojang.serialization.{DataResult, DynamicOps, MapCodec, RecordBuilder}
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.bdew.lib.inventory.NullInventory
import net.bdew.lib.registries.RegistryObject
import net.minecraft.core.{HolderLookup, RegistryAccess}
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.{Recipe, RecipeSerializer, RecipeType}
import net.minecraft.world.level.Level

abstract class BaseMachineRecipe(val id: ResourceLocation) extends Recipe[NullInventory] {
  override def matches(inv: NullInventory, world: Level): Boolean = true
  override def assemble(inv: NullInventory, registries: HolderLookup.Provider): ItemStack = ItemStack.EMPTY
  override def canCraftInDimensions(x: Int, y: Int): Boolean = true
  override def getResultItem(registries: HolderLookup.Provider): ItemStack = ItemStack.EMPTY
  override def isSpecial: Boolean = true
}

abstract class BaseMachineRecipeSerializer[T <: BaseMachineRecipe] extends RecipeSerializer[T] {
  def fromJson(recipeId: ResourceLocation, obj: JsonObject): T
  def fromNetwork(recipeId: ResourceLocation, buff: net.minecraft.network.FriendlyByteBuf): T
  def toNetwork(buffer: net.minecraft.network.FriendlyByteBuf, recipe: T): Unit

  /**
   * 1.21 recipe serializers are codec-based. The mod still uses its historical
   * JSON format, so this adapter reads a MapLike as a JsonObject. Recipe ids are
   * no longer available at decode time; the id is only used for logging here.
   */
  override def codec(): MapCodec[T] = new MapCodec[T] {
    override def decode[O](ops: DynamicOps[O], input: com.mojang.serialization.MapLike[O]): DataResult[T] = {
      try {
        val obj = new JsonObject()
        val it = input.entries().iterator()
        while (it.hasNext) {
          val entry = it.next()
          val key = ops.getStringValue(entry.getFirst).getOrThrow()
          obj.add(key, ops.convertTo(com.mojang.serialization.JsonOps.INSTANCE, entry.getSecond))
        }
        DataResult.success(fromJson(null, obj))
      } catch {
        case e: Exception => DataResult.error[T](() => String.valueOf(e.getMessage))
      }
    }

    override def encode[O](input: T, ops: DynamicOps[O], prefix: RecordBuilder[O]): RecordBuilder[O] = prefix

    override def keys[O](ops: DynamicOps[O]): java.util.stream.Stream[O] = java.util.stream.Stream.empty()
  }

  override def streamCodec(): StreamCodec[RegistryFriendlyByteBuf, T] = StreamCodec.of(
    (buf, recipe) => toNetwork(buf, recipe),
    buf => fromNetwork(null, buf)
  )
}

class MachineRecipeType[T <: BaseMachineRecipe](reg: RegistryObject[_ <: RecipeSerializer[T]]) extends RecipeType[T] {
  val registryName: ResourceLocation = reg.getId
  override def toString: String = registryName.toString
}
