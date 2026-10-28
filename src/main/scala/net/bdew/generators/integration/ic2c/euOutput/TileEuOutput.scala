package net.bdew.generators.integration.ic2c.euOutput

import ic2.api.energy.EnergyNet
import ic2.api.energy.tile.{IEnergyAcceptor, IEnergySource}
import net.bdew.generators.config.Config
import net.bdew.generators.controllers.CIPowerAccess
import net.bdew.generators.integration.ic2c.IC2Tier
import net.bdew.generators.registries.Modules
import net.bdew.lib.multiblock.ModuleType
import net.bdew.lib.multiblock.data.OutputConfigPower
import net.bdew.lib.multiblock.interact.CIPowerOutput
import net.bdew.lib.multiblock.tile.{RSControllableOutput, TileOutput, TileOutputTracker}
import net.minecraft.core.{BlockPos, Direction}
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TileEuOutput(teType: BlockEntityType[_], pos: BlockPos, state: BlockState, tier: IC2Tier) extends TileOutput[OutputConfigPower](teType, pos, state) with RSControllableOutput with IEnergySource {
  val kind: ModuleType = Modules.powerOutput

  override def getCore: Option[CIPowerOutput with CIPowerAccess] = getCoreAs[CIPowerOutput with CIPowerAccess]

  override val outputConfigType: Class[OutputConfigPower] = classOf[OutputConfigPower]
  override def makeCfgObject(face: Direction) = new OutputConfigPower("eu")

  val outputTracker: TileOutputTracker[OutputConfigPower] = new TileOutputTracker(this, _.updateAvg(_))

  private var addedToNetwork = false

  private def addToNetwork(): Unit = {
    if (!addedToNetwork && getLevel != null && !getLevel.isClientSide) {
      EnergyNet.instance.addBlockEntityTile(this)
      addedToNetwork = true
    }
  }

  private def removeFromNetwork(): Unit = {
    if (addedToNetwork) {
      EnergyNet.instance.removeTile(this)
      addedToNetwork = false
    }
  }

  override def onLoad(): Unit = {
    super.onLoad()
    addToNetwork()
  }

  override def setRemoved(): Unit = {
    removeFromNetwork()
    super.setRemoved()
  }

  override def onChunkUnloaded(): Unit = {
    removeFromNetwork()
    super.onChunkUnloaded()
  }

  def ratio: Double = Config.Integration.IC2.ratio()
  def facing: Direction = getBlockState.getBlock.asInstanceOf[BlockEuOutput].getFacing(getLevel.getBlockState(getBlockPos))

  override def getSourceTier: Int = tier.number

  override def getOfferedEnergy: Double = {
    val out = for {
      core <- getCore
      cfg <- getCfg(facing) if checkCanOutput(cfg)
    } yield (core.power.stored * ratio).floor
    Math.min(out.getOrElse(0D), tier.maxPower)
  }

  override def drawEnergy(amount: Double): Unit = {
    if (ratio > 0)
      getCore.foreach(c => c.power.extract((amount / ratio).toFloat, false))
    outputTracker.track(facing, amount.toFloat)
  }

  override def emitsEnergyTo(acceptor: IEnergyAcceptor, direction: Direction): Boolean =
    getCore.isDefined && facing == direction

  override def canConnectToFace(d: Direction): Boolean =
    d == facing

  def facingChanged(): Unit = {
    doRescanFaces()
    if (addedToNetwork) {
      EnergyNet.instance.removeTile(this)
      EnergyNet.instance.addBlockEntityTile(this)
    }
  }

  override def doOutput(face: Direction, cfg: OutputConfigPower): Unit = {}
}
