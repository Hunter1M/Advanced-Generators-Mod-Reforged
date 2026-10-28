package net.bdew.generators.integration.electrodynamics.output

import electrodynamics.prefab.utilities.ElectricityUtils
import net.bdew.generators.config.Config
import net.bdew.generators.controllers.CIPowerAccess
import net.bdew.generators.registries.Modules
import net.bdew.lib.multiblock.ModuleType
import net.bdew.lib.multiblock.data.OutputConfigPower
import net.bdew.lib.multiblock.interact.CIPowerOutput
import net.bdew.lib.multiblock.misc.TileForcedOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput, TileOutputTracker}
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.state.BlockState
import voltaic.prefab.utilities.`object`.TransferPack
import voltaic.registers.VoltaicCapabilities

class TileElectrodynamicsOutput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState)
  extends TileOutput[OutputConfigPower](teType, pos, state) with RSControllableOutput with TileForcedOutput {

  val kind: ModuleType = Modules.powerOutput

  override def getCore: Option[CIPowerOutput with CIPowerAccess] = getCoreAs[CIPowerOutput with CIPowerAccess]

  override val outputConfigType: Class[OutputConfigPower] = classOf[OutputConfigPower]
  override def makeCfgObject(face: Direction) = new OutputConfigPower("j")

  val outputTracker: TileOutputTracker[OutputConfigPower] = new TileOutputTracker(this, _.updateAvg(_))

  def getTarget(d: Direction): Option[BlockEntity] = {
    val target = getBlockPos.offset(d.getNormal)
    val be = getLevel.getBlockEntity(target)
    if (be != null && ElectricityUtils.isElectricReceiver(be, d.getOpposite)) Some(be) else None
  }

  override def canConnectToFace(d: Direction): Boolean =
    forcedSides(d) || getTarget(d).isDefined

  override def doOutput(face: Direction, cfg: OutputConfigPower): Unit = {
    for {
      core <- getCore if checkCanOutput(cfg)
      target <- getTarget(face)
    } {
      val joulesPerFe = Config.Integration.Electrodynamics.joulesPerFe()
      if (joulesPerFe > 0) {
        val maxJoules = Math.floor(core.power.stored * joulesPerFe)
        if (maxJoules > 0) {
          val side = face.getOpposite
          val preferred = Config.Integration.Electrodynamics.voltage()
          val electrodynamic = getLevel.getCapability(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK,
            target.getBlockPos, side)
          val voltage =
            if (electrodynamic != null && electrodynamic.getMinimumVoltage > 0 &&
              electrodynamic.getMaximumVoltage >= electrodynamic.getMinimumVoltage)
              Math.max(electrodynamic.getMinimumVoltage,
                Math.min(preferred, electrodynamic.getMaximumVoltage))
            else
              Math.min(preferred, 120.0D) // Electrodynamics' FE bridge is safe up to 120 V

          val offered = TransferPack.joulesVoltage(maxJoules, voltage)
          val simulated = ElectricityUtils.receivePower(target, side, offered, true)
          if (simulated.getJoules > 0) {
            val accepted = ElectricityUtils.receivePower(target, side, offered, false)
            val joules = accepted.getJoules
            if (joules > 0) {
              core.power.extract((joules / joulesPerFe).toFloat, false)
              outputTracker.track(face, joules.toFloat)
            }
          }
        }
      }
    }
  }
}
