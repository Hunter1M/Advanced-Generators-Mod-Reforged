package net.bdew.lib.capabilities.helpers

import net.bdew.lib.capabilities.adapters.InventoryAdapter
import net.bdew.lib.capabilities.{CapAdapters, CapabilityProvider}
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

object ItemHelper {
  CapAdapters.add(Capabilities.ItemHandler.BLOCK, InventoryAdapter)

  def hasItemHandler(world: Level, pos: BlockPos, side: Direction): Boolean =
    getItemHandler(world, pos, side).isDefined

  def hasItemHandler(stack: ItemStack): Boolean =
    getItemHandler(stack).isDefined

  def getItemHandler(world: Level, pos: BlockPos, side: Direction): Option[IItemHandler] = {
    Option(world.getCapability(Capabilities.ItemHandler.BLOCK, pos, side)).orElse {
      val tile = world.getBlockEntity(pos)
      if (tile == null) None
      else CapAdapters.get(Capabilities.ItemHandler.BLOCK).wrap(tile, side)
    }
  }

  def getItemHandler(stack: ItemStack): Option[IItemHandler] = {
    if (stack.isEmpty) return None
    Option(stack.getCapability(Capabilities.ItemHandler.ITEM)).orElse(
      CapAdapters.get(Capabilities.ItemHandler.ITEM).wrap(stack)
    )
  }

  def pushItems(from: IItemHandler, to: IItemHandler, maxItems: Int = Int.MaxValue, filter: ItemStack => Boolean = !_.isEmpty): Unit = {
    var itemsPushed = 0
    for {
      fromSlot <- 0 until from.getSlots if filter(from.getStackInSlot(fromSlot))
      toSlot <- 0 until to.getSlots
    } {
      val canExtract = from.extractItem(fromSlot, maxItems - itemsPushed, true)
      if (!canExtract.isEmpty) {
        val leftAfterInsert = to.insertItem(toSlot, canExtract, false)
        if (leftAfterInsert == null) {
          from.extractItem(fromSlot, canExtract.getCount, false)
          itemsPushed += canExtract.getCount
        } else {
          from.extractItem(fromSlot, canExtract.getCount - leftAfterInsert.getCount, false)
          itemsPushed += canExtract.getCount - leftAfterInsert.getCount
        }
        if (itemsPushed >= maxItems) return
      }
    }
  }
}
