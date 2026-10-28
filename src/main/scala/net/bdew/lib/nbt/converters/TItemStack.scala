package net.bdew.lib.nbt.converters

import net.bdew.lib.nbt.ConvertedType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.{CompoundTag, Tag}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{ItemStack, Items}

object TItemStack extends ConvertedType[ItemStack, CompoundTag] {
  override def encode(v: ItemStack): CompoundTag = {
    val tag = new CompoundTag()
    tag.putString("id", BuiltInRegistries.ITEM.getKey(v.getItem).toString)
    tag.putByte("Count", v.getCount.toByte)
    val custom = v.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA)
    if (custom != null && !custom.isEmpty)
      tag.put("tag", custom.getUnsafe.copy())
    tag
  }

  override def decode(v: CompoundTag): Option[ItemStack] = {
    if (!v.contains("id", Tag.TAG_STRING)) return None
    val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(v.getString("id")))
    if (item == null || item == Items.AIR) return None
    val count = Math.max(1, v.getByte("Count").toInt)
    val stack = new ItemStack(item, count)
    if (v.contains("tag", Tag.TAG_COMPOUND))
      stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(v.getCompound("tag")))
    Some(stack)
  }
}
