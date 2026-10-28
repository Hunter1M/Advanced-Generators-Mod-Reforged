package net.bdew.lib.inventory

import net.bdew.lib.PimpVanilla._
import net.bdew.lib.nbt.NBT
import net.minecraft.nbt.{CompoundTag, ListTag}
import net.minecraft.world.item.ItemStack

class StackInventory(stack: ItemStack, size: Int) extends BaseInventory {
  override def getContainerSize: Int = size

  if (stack.hasTag && stack.getTag.contains("Inventory")) {
    for (nbtItem <- stack.getTag.getListVals[CompoundTag]("Inventory")) {
      val slot = nbtItem.getByte("Slot")
      if (slot >= 0 && slot < inv.length) {
        inv(slot) = net.bdew.lib.nbt.converters.TItemStack.decode(nbtItem).getOrElse(ItemStack.EMPTY)
      }
    }
  }

  override def setChanged(): Unit = {
    val itemList = new ListTag()
    for ((item, i) <- inv.view.zipWithIndex if !item.isEmpty) {
      itemList.add(NBT.from(itemNbt => {
        itemNbt.putByte("Slot", i.asInstanceOf[Byte])
        itemNbt.merge(net.bdew.lib.nbt.converters.TItemStack.encode(item))
      }))
    }
    stack.getOrCreateTag().put("Inventory", itemList)
  }
}
