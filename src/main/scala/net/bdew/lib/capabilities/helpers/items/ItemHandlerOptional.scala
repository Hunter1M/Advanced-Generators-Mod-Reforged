package net.bdew.lib.capabilities.helpers.items

import net.neoforged.neoforge.items.IItemHandler

class ItemHandlerOptional(handler: () => Option[IItemHandler]) extends ItemHandlerProxy {
  override def base: IItemHandler = handler().getOrElse(ItemHandlerNull)
}

object ItemHandlerOptional {
  def create(base: () => Option[IItemHandler]): IItemHandler = new ItemHandlerOptional(base)
}
