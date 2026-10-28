package net.bdew.lib.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.{BlockItem, ItemStack, TooltipFlag}
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

import java.util

trait TooltipBlockItem extends BlockItem {
  override def appendHoverText(stack: ItemStack, context: Item.TooltipContext, toolTip: util.List[Component], flags: TooltipFlag): Unit = {
    import scala.jdk.CollectionConverters._
    toolTip.addAll(getTooltip(stack, context.level(), flags).asJavaCollection)
    super.appendHoverText(stack, context, toolTip, flags)
  }

  def getTooltip(stack: ItemStack, world: Level, flags: TooltipFlag): List[Component] = List.empty
}
