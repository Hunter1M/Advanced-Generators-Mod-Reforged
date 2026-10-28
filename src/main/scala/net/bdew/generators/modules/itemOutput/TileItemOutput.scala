package net.bdew.generators.modules.itemOutput

import net.bdew.generators.registries.Modules
import net.bdew.lib.capabilities.helpers.ItemHelper
import net.bdew.lib.capabilities.helpers.items.ItemHandlerOptional
import net.bdew.lib.capabilities.SidedCapHandler
import net.bdew.lib.multiblock.ModuleType
import net.bdew.lib.multiblock.data.OutputConfigItems
import net.bdew.lib.multiblock.interact.CIItemOutput
import net.bdew.lib.multiblock.misc.TileForcedOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput}
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

class TileItemOutput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState) extends TileOutput[OutputConfigItems](teType, pos, state) with RSControllableOutput with TileForcedOutput {
  val kind: ModuleType = Modules.itemOutput

  override val outputConfigType: Class[OutputConfigItems] = classOf[OutputConfigItems]
  override def makeCfgObject(face: Direction) = new OutputConfigItems
  override def getCore: Option[CIItemOutput] = super.getCoreAs[CIItemOutput]

  def outputSourceForSide(side: Direction): Option[IItemHandler] =
    getCore.flatMap(core =>
      getCfg(side) match {
        case Some(cfg) if checkCanOutput(cfg) => Some(core.itemOutput)
        case _ => None
      }
    )

  val itemHandlers = new SidedCapHandler[IItemHandler](side =>
    new ItemHandlerOptional(() => outputSourceForSide(side)), allowNull = false)

  override def itemHandler(side: Direction): IItemHandler = itemHandlers.get(side)

  def getTargetItemHandler(d: Direction): Option[IItemHandler] = {
    Option(getLevel.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos.offset(d.getNormal), d.getOpposite))
  }

  override def canConnectToFace(d: Direction): Boolean =
    getCore.isDefined && (forcedSides(d) || getTargetItemHandler(d).isDefined)

  override def doOutput(face: Direction, cfg: OutputConfigItems): Unit = {
    for {
      source <- outputSourceForSide(face) if checkCanOutput(cfg)
      target <- getTargetItemHandler(face)
    } {
      ItemHelper.pushItems(source, target)
    }
  }
}
