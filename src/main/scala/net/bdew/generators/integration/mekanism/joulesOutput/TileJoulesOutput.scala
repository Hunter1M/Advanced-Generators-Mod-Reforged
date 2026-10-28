package net.bdew.generators.integration.mekanism.joulesOutput

import mekanism.api.Action
import mekanism.api.energy.IStrictEnergyHandler
import mekanism.common.capabilities.Capabilities
import net.bdew.generators.config.Config
import net.bdew.generators.controllers.CIPowerAccess
import net.bdew.generators.registries.Modules
import net.bdew.lib.multiblock.ModuleType
import net.bdew.lib.multiblock.data.OutputConfigPower
import net.bdew.lib.multiblock.interact.CIPowerOutput
import net.bdew.lib.multiblock.misc.TileForcedOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput, TileOutputTracker}
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TileJoulesOutput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState)
  extends TileOutput[OutputConfigPower](teType, pos, state) with RSControllableOutput with TileForcedOutput {

  val kind: ModuleType = Modules.powerOutput

  override def getCore: Option[CIPowerOutput with CIPowerAccess] = getCoreAs[CIPowerOutput with CIPowerAccess]

  override val outputConfigType: Class[OutputConfigPower] = classOf[OutputConfigPower]
  override def makeCfgObject(face: Direction) = new OutputConfigPower("j")

  val outputTracker: TileOutputTracker[OutputConfigPower] = new TileOutputTracker(this, _.updateAvg(_))

  def getTargetJoulesHandlers(d: Direction): List[IStrictEnergyHandler] = {
    val target = getBlockPos.offset(d.getNormal)
    // Mekanism exposes the strict energy handler unsided for most machines; some
    // blocks only expose a specific face, so try both.
    List(
      Option(getLevel.getCapability(Capabilities.STRICT_ENERGY.block(), target, null)),
      Option(getLevel.getCapability(Capabilities.STRICT_ENERGY.block(), target, d.getOpposite))
    ).flatten.distinct
  }

  override def canConnectToFace(d: Direction): Boolean =
    forcedSides(d) || getTargetJoulesHandlers(d).nonEmpty

  override def doOutput(face: Direction, cfg: OutputConfigPower): Unit = {
    for {
      core <- getCore if checkCanOutput(cfg)
      target <- getTargetJoulesHandlers(face)
    } {
      val fePerJoule = Config.Integration.Mekanism.fePerJoule()
      if (fePerJoule > 0) {
        val maxJoules = Math.floor(core.power.stored / fePerJoule).toLong
        if (maxJoules > 0 && target.insertEnergy(maxJoules, Action.SIMULATE) > 0) {
          val inserted = target.insertEnergy(maxJoules, Action.EXECUTE)
          if (inserted > 0) {
            core.power.extract((inserted * fePerJoule).toFloat, false)
            outputTracker.track(face, inserted.toFloat)
          }
        }
      }
    }
  }
}
