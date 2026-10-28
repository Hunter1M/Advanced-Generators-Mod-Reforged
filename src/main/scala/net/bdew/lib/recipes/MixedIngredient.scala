package net.bdew.lib.recipes

import com.google.gson.{JsonObject, JsonSyntaxException}
import com.mojang.serialization.JsonOps
import net.bdew.lib.resource.{FluidResource, ItemResource, Resource}
import net.minecraft.network.{FriendlyByteBuf, RegistryFriendlyByteBuf}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.fluids.FluidStack

trait MixedIngredient {
  def matches(is: ItemStack): Boolean
  def matches(fs: FluidStack): Boolean
  def matches(res: Resource): Boolean
  def toPacket(pkt: FriendlyByteBuf): Unit
}

object MixedIngredient {
  case class Item(ingredient: Ingredient) extends MixedIngredient {
    override def matches(is: ItemStack): Boolean = ingredient.test(is)
    override def matches(fs: FluidStack): Boolean = false

    override def matches(res: Resource): Boolean = res match {
      case Resource(i: ItemResource, _) => ingredient.test(i.makeStack(1))
      case _ => false
    }

    override def toPacket(pkt: FriendlyByteBuf): Unit = {
      pkt.writeUtf("item")
      Ingredient.CONTENTS_STREAM_CODEC.encode(pkt.asInstanceOf[RegistryFriendlyByteBuf], ingredient)
    }
  }

  case class Fluid(ingredient: FluidIngredient) extends MixedIngredient {
    override def matches(is: ItemStack): Boolean = false
    override def matches(fs: FluidStack): Boolean = ingredient.matches(fs)
    override def matches(res: Resource): Boolean = res match {
      case Resource(i: FluidResource, _) => ingredient.matches(i.fluid)
      case _ => false
    }
    override def toPacket(pkt: FriendlyByteBuf): Unit = {
      pkt.writeUtf("fluid")
      ingredient.toPacket(pkt)
    }
  }

  def apply(v: Ingredient): MixedIngredient = Item(v)
  def apply(v: FluidIngredient): MixedIngredient = Fluid(v)

  def fromPacket(pkt: FriendlyByteBuf): MixedIngredient = {
    pkt.readUtf() match {
      case "item" => Item(Ingredient.CONTENTS_STREAM_CODEC.decode(pkt.asInstanceOf[RegistryFriendlyByteBuf]))
      case "fluid" => Fluid(FluidIngredient.fromPacket(pkt))
      case x => throw new RuntimeException(s"Unknown ingredient type $x")
    }
  }

  def fromJson(js: JsonObject): MixedIngredient = {
    if (js.has("items"))
      Item(Ingredient.CODEC.parse(JsonOps.INSTANCE, js.get("items")).getOrThrow())
    else if (js.has("fluids"))
      Fluid(FluidIngredient.fromJson(js.get("fluids")))
    else throw new JsonSyntaxException("Expected fluids or items ingredient")
  }
}