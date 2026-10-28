package net.bdew.lib.block

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.{InteractionHand, InteractionResult, ItemInteractionResult}

trait WrenchableBlock extends Block {
  def wrenched(state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, stack: ItemStack, hit: BlockHitResult): InteractionResult

  override def useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult = {
    if (!stack.isEmpty && WrenchableBlock.isWrench(stack)) {
      val res = wrenched(state, world, pos, player, hand, stack, hit)
      if (res != InteractionResult.PASS)
        return WrenchableBlock.toItemResult(res)
    }
    super.useItemOn(stack, state, world, pos, player, hand, hit)
  }
}

object WrenchableBlock {
  val wrenchTags = Set(
    ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "wrenches")),
    ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench")),
  )

  def isWrench(stack: ItemStack): Boolean = wrenchTags.exists(stack.is)

  def toItemResult(res: InteractionResult): ItemInteractionResult =
    if (res == InteractionResult.SUCCESS) ItemInteractionResult.SUCCESS
    else if (res == InteractionResult.CONSUME) ItemInteractionResult.CONSUME
    else if (res == InteractionResult.CONSUME_PARTIAL) ItemInteractionResult.CONSUME_PARTIAL
    else if (res == InteractionResult.FAIL) ItemInteractionResult.FAIL
    else ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
}
