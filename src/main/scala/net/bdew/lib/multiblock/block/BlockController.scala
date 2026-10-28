package net.bdew.lib.multiblock.block

import net.bdew.lib.block.HasTETickingServer
import net.bdew.lib.multiblock.tile.{TileController, TileModule}
import net.bdew.lib.render.connected.ConnectedTextureBlock
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.{BlockBehaviour, BlockState}
import net.minecraft.world.level.{BlockGetter, Level}
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.{InteractionHand, ItemInteractionResult}

abstract class BlockController[T <: TileController](props: BlockBehaviour.Properties)
  extends Block(props) with HasTETickingServer[T] with ConnectedTextureBlock {

  override def setPlacedBy(world: Level, pos: BlockPos, state: BlockState, placer: LivingEntity, stack: ItemStack): Unit = {
    super.setPlacedBy(world, pos, state, placer, stack)
    // Modules placed before the controller have no way to discover it otherwise.
    if (!world.isClientSide) {
      Direction.values().foreach { dir =>
        world.getBlockEntity(pos.offset(dir.getNormal)) match {
          case module: TileModule => module.tryConnect()
          case _ =>
        }
      }
    }
  }

  override def onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, moving: Boolean): Unit = {
    getTE(world, pos).onBreak()
    super.onRemove(state, world, pos, newState, moving)
  }

  override def canConnect(world: BlockGetter, origin: BlockPos, target: BlockPos): Boolean =
    getTE(world, origin).exists(_.modules.contains(target))

  override def useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult = {
    if (player.isCrouching) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    if (world.isClientSide) {
      ItemInteractionResult.SUCCESS
    } else {
      getTE(world, pos).onClick(player)
      ItemInteractionResult.CONSUME
    }
  }
}


