package net.bdew.lib.resource

import net.bdew.lib.gui.{Color, Texture}
import net.bdew.lib.{Client, Text}
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.{Component, MutableComponent}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{Item, ItemStack}
import net.neoforged.api.distmarker.{Dist, OnlyIn}
import net.minecraft.core.registries.BuiltInRegistries

case class ItemResource(item: Item) extends ResourceKind {
  override def getTexture: Texture = {
    Texture.block(Client.minecraft.getItemRenderer.getItemModelShaper.getItemModel(item).getParticleIcon)
  }

  @OnlyIn(Dist.CLIENT)
  override def getColor: Color = Color.fromInt(Client.itemColors.getColor(makeStack(1), 0))

  override def getName: Component = item.getDescription

  override def getFormattedString(amount: Double, capacity: Double): MutableComponent =
    Text.withCap(amount, capacity)

  override def capacityMultiplier: Double = 1 / 250D

  def makeStack(n: Int) = new ItemStack(item, n)

  override def toString: String = "ItemResource(%s)".format(BuiltInRegistries.ITEM.getKey(item))
  override def helperObject: ResourceHelper[_ >: ItemResource.this.type] = ItemResourceHelper
}

object ItemResourceHelper extends ResourceHelper[ItemResource]("item") {
  override def loadFromNBT(tag: CompoundTag): Option[ItemResource] = {
    val item = Option(BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("id"))))
    item.map(ItemResource)
  }
  override def saveToNBT(tag: CompoundTag, r: ItemResource): Unit = {
    tag.putString("id", BuiltInRegistries.ITEM.getKey(r.item).toString)
  }
}

