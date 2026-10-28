package net.bdew.lib.capabilities.handlers

import net.minecraft.world.WorldlyContainer
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper

object SidedInventoryItemHandler {
  def create(inv: WorldlyContainer): IItemHandler = {
    new SidedInvWrapper(inv, null)
  }
}