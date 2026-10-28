package net.bdew.lib.recipes

import com.google.gson.{JsonObject, JsonSyntaxException}
import net.bdew.lib.JSResLoc
import net.bdew.lib.resource.{FluidResource, ItemResource, Resource, ResourceKind}
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.minecraft.core.registries.BuiltInRegistries

trait ResourceOutput {
  def resource(amount: Double): Resource = Resource(resourceKind, amount)
  def resourceKind: ResourceKind
  def toPacket(pkt: FriendlyByteBuf): Unit
}

object ResourceOutput {
  case class ItemOutput(item: Item) extends ResourceOutput {
    override def resourceKind: ResourceKind = ItemResource(item)
    override def toPacket(pkt: FriendlyByteBuf): Unit = {
      pkt.writeUtf("item")
      pkt.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item))
    }
  }

  case class FluidOutput(fluid: Fluid) extends ResourceOutput {
    override def resourceKind: ResourceKind = FluidResource(fluid)
    override def toPacket(pkt: FriendlyByteBuf): Unit = {
      pkt.writeUtf("fluid")
      pkt.writeResourceLocation(BuiltInRegistries.FLUID.getKey(fluid))
    }
  }

  def apply(v: Item): ResourceOutput = ItemOutput(v)
  def apply(v: Fluid): ResourceOutput = FluidOutput(v)

  def fromPacket(pkt: FriendlyByteBuf): ResourceOutput = {
    pkt.readUtf() match {
      case "item" => ItemOutput(BuiltInRegistries.ITEM.get(pkt.readResourceLocation()))
      case "fluid" => FluidOutput(BuiltInRegistries.FLUID.get(pkt.readResourceLocation()))
      case x => throw new RuntimeException(s"Unknown output type $x")
    }
  }

  def fromJson(js: JsonObject): ResourceOutput = {
    js.get("item") match {
      case JSResLoc(key) if BuiltInRegistries.ITEM.containsKey(key) =>
        ItemOutput(BuiltInRegistries.ITEM.get(key))
      case _ =>
        js.get("fluid") match {
          case JSResLoc(key) if BuiltInRegistries.FLUID.containsKey(key) =>
            FluidOutput(BuiltInRegistries.FLUID.get(key))
          case _ => throw new JsonSyntaxException("Invalid fluid or item output")
        }
    }
  }
}
