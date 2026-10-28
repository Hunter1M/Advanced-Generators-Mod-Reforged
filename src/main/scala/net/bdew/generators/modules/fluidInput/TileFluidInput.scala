package net.bdew.generators.modules.fluidInput

import net.bdew.generators.registries.Modules
import net.bdew.lib.capabilities.helpers.fluid.FluidHandlerOptional
import net.bdew.lib.multiblock.ModuleType
import net.bdew.lib.multiblock.interact.CIFluidInput
import net.bdew.lib.multiblock.tile.TileModule
import net.bdew.lib.tile.TileExtended
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class TileFluidInput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState) extends TileExtended(teType, pos, state) with TileModule {
  val kind: ModuleType = Modules.fluidInput

  override def getCore: Option[CIFluidInput] = getCoreAs[CIFluidInput]

  val handler: IFluidHandler = FluidHandlerOptional.create(() => getCore.map(_.fluidInput))

  override def fluidHandler(side: Direction): IFluidHandler = handler
}
