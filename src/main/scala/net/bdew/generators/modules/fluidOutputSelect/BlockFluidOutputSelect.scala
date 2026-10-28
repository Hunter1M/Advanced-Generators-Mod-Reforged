package net.bdew.generators.modules.fluidOutputSelect

import net.bdew.generators.modules.BaseModule
import net.bdew.generators.registries.Modules
import net.bdew.lib.capabilities.helpers.FluidHelper
import net.bdew.lib.multiblock.block.BlockOutput
import net.bdew.lib.multiblock.misc.BlockForcedOutput
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.{BlockGetter, Level}
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.{InteractionHand, ItemInteractionResult}

class BlockFluidOutputSelect extends BaseModule[TileFluidOutputSelect](Modules.fluidOutput) with BlockOutput[TileFluidOutputSelect] with BlockForcedOutput {
  override def canConnectRedstone(state: BlockState, world: BlockGetter, pos: BlockPos, side: Direction): Boolean = true

  override def useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult = {
    if (!world.isClientSide) {
      if (FluidHelper.blockFluidInteract(world, pos, player, hand))
        return ItemInteractionResult.CONSUME
    }
    super.useItemOn(stack, state, world, pos, player, hand, hit)
  }
}
