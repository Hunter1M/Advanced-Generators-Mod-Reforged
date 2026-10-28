package net.bdew.generators.integration.buildcraft.mjOutput

import buildcraft.lib.internal.mj.{IMjReceiver, MjCapabilities}
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
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction

class TileMjOutput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState)
  extends TileOutput[OutputConfigPower](teType, pos, state) with RSControllableOutput with TileForcedOutput {

  val kind: ModuleType = Modules.powerOutput

  override def getCore: Option[CIPowerOutput with CIPowerAccess] = getCoreAs[CIPowerOutput with CIPowerAccess]

  override val outputConfigType: Class[OutputConfigPower] = classOf[OutputConfigPower]
  override def makeCfgObject(face: Direction) = new OutputConfigPower("mj")

  val outputTracker: TileOutputTracker[OutputConfigPower] = new TileOutputTracker(this, _.updateAvg(_))

  def getTargetMjReceiver(d: Direction): Option[IMjReceiver] =
    Option(getLevel.getCapability(MjCapabilities.CAP_RECEIVER, getBlockPos.offset(d.getNormal), d.getOpposite))

  override def canConnectToFace(d: Direction): Boolean =
    forcedSides(d) || getTargetMjReceiver(d).isDefined

  override def doOutput(face: Direction, cfg: OutputConfigPower): Unit = {
    for {
      core <- getCore if checkCanOutput(cfg)
      target <- getTargetMjReceiver(face)
    } {
      val mjPerFe = Config.Integration.BuildCraft.mjPerFe()
      if (mjPerFe > 0) {
        val maxMj = Math.floor(core.power.stored * mjPerFe).toLong
        if (maxMj > 0) {
          val accepted = target.receivePower(maxMj, FluidAction.EXECUTE)
          if (accepted > 0) {
            core.power.extract((accepted / mjPerFe).toFloat, false)
            outputTracker.track(face, accepted.toFloat)
          }
        }
      }
    }
  }
}
