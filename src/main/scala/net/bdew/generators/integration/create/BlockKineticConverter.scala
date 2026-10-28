package net.bdew.generators.integration.create

import com.simibubi.create.content.equipment.wrench.IWrenchable
import com.simibubi.create.content.kinetics.base.{KineticBlock, KineticBlockEntity}
import net.bdew.generators.registries.Blocks
import net.bdew.lib.block.HasTE
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.{BlockPlaceContext, UseOnContext}
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityTicker, BlockEntityType}
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.{BlockState, StateDefinition}
import net.minecraft.world.level.Level

class BlockKineticConverter extends KineticBlock(Blocks.machineProps) with HasTE[TileKineticConverter] {
  override def getRotationAxis(state: BlockState): Direction.Axis =
    state.getValue(BlockStateProperties.AXIS)

  override def hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean =
    face.getAxis == getRotationAxis(state)

  override def getStateForPlacement(ctx: BlockPlaceContext): BlockState =
    super.getStateForPlacement(ctx).setValue(BlockStateProperties.AXIS, ctx.getClickedFace.getAxis)

  override def onWrenched(state: BlockState, context: UseOnContext): InteractionResult = {
    val nextAxis = state.getValue(BlockStateProperties.AXIS) match {
      case Direction.Axis.X => Direction.Axis.Y
      case Direction.Axis.Y => Direction.Axis.Z
      case Direction.Axis.Z => Direction.Axis.X
    }
    if (!context.getLevel.isClientSide) {
      val newState = state.setValue(BlockStateProperties.AXIS, nextAxis)
      // Use Create's kinetic-aware state switch so the old network is detached,
      // the source is re-activated, and the new axis is propagated immediately.
      KineticBlockEntity.switchToBlockState(context.getLevel, context.getClickedPos, newState)
      context.getLevel.getBlockEntity(context.getClickedPos) match {
        case kinetic: TileKineticConverter =>
          kinetic.updateGeneratedRotation()
          kinetic.sendData()
        case _ =>
      }
      IWrenchable.playRotateSound(context.getLevel, context.getClickedPos)
    }
    InteractionResult.SUCCESS
  }

  override def createBlockStateDefinition(builder: StateDefinition.Builder[net.minecraft.world.level.block.Block, BlockState]): Unit = {
    super.createBlockStateDefinition(builder)
    builder.add(BlockStateProperties.AXIS)
  }

  private val converterTicker = new KineticConverterTicker

  override def getTicker[E <: BlockEntity](world: Level, state: BlockState, teType: BlockEntityType[E]): BlockEntityTicker[E] = {
    if (world.isClientSide) null else converterTicker.asInstanceOf[BlockEntityTicker[E]]
  }
}

