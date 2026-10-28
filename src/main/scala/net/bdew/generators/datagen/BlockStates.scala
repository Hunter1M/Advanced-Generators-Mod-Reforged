package net.bdew.generators.datagen

import net.bdew.generators.Generators
import net.bdew.generators.integration.create.BlockKineticConverter
import net.bdew.generators.modules.control.BlockControl
import net.bdew.generators.registries.Blocks
import net.bdew.lib.datagen.BlockStateGenerator
import net.bdew.lib.rotate.StatefulBlockFacing
import net.bdew.lib.sensors.multiblock.BlockRedstoneSensorModule
import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.minecraft.core.registries.BuiltInRegistries

class BlockStates(gen: PackOutput, efh: ExistingFileHelper) extends BlockStateGenerator(gen, Generators.ModId, efh) {
  override def registerStatesAndModels(): Unit = {
    Blocks.all.foreach(_.get() match {
      case block: BlockControl =>
        val modelOff = uncheckedModel("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath + "_off")
        val modelOn = uncheckedModel("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath + "_on")
        getVariantBuilder(block)
          .forAllStates(state => {
            val powered = state.getValue(BlockStateProperties.POWERED)
            ConfiguredModel.builder()
              .modelFile(if (powered) modelOn else modelOff)
              .build()
          })
        makeBlockItem(block, modelOff)
      case block: BlockRedstoneSensorModule[_] =>
        val modelOff = uncheckedModel("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath + "_off")
        val modelOn = uncheckedModel("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath + "_on")
        getVariantBuilder(block)
          .forAllStates(state => {
            val facing = state.getValue(BlockStateProperties.FACING)
            val powered = state.getValue(BlockStateProperties.POWERED)
            val builder = ConfiguredModel.builder()
              .modelFile(if (powered) modelOn else modelOff)
            facing match {
              case Direction.UP => builder.rotationX(270)
              case Direction.DOWN => builder.rotationX(90)
              case Direction.SOUTH => builder.rotationY(180)
              case Direction.WEST => builder.rotationY(270)
              case Direction.EAST => builder.rotationY(90)
              case Direction.NORTH => //nothing
            }
            builder.build()
          })
        makeBlockItem(block, modelOff)
      case block: BlockKineticConverter =>
        val model = uncheckedModel("block/kinetic_converter")
        getVariantBuilder(block)
          .forAllStates(state => {
            val axis = state.getValue(BlockStateProperties.AXIS)
            val builder = ConfiguredModel.builder().modelFile(model)
            axis match {
              case Direction.Axis.X =>
                builder.rotationX(90).rotationY(90)
              case Direction.Axis.Z =>
                builder.rotationX(90).rotationY(180)
              case _ =>
            }
            builder.build()
          })
        makeBlockItem(block, model)
      case block: StatefulBlockFacing =>
        val model = uncheckedModel("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath)
        getVariantBuilder(block)
          .forAllStates(state => {
            val facing = block.getFacing(state)
            val builder = ConfiguredModel.builder()
              .modelFile(model)
            facing match {
              case Direction.UP => builder.rotationX(270)
              case Direction.DOWN => builder.rotationX(90)
              case Direction.SOUTH => builder.rotationY(180)
              case Direction.WEST => builder.rotationY(270)
              case Direction.EAST => builder.rotationY(90)
              case Direction.NORTH => //nothing
            }
            builder.build()
          })
        makeBlockItem(block, model)
      case x => makeBlock(x)
    })
  }
}
