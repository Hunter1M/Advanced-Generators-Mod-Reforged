package net.bdew.generators.integration.create

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import net.bdew.generators.config.Config
import net.minecraft.core.{BlockPos, HolderLookup}
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.{EnergyStorage, IEnergyStorage}

import java.util.{List => JList}

class TileKineticConverter(teType: BlockEntityType[_], pos: BlockPos, state: BlockState)
  extends GeneratingKineticBlockEntity(teType, pos, state) {

  private val cfg = Config.Integration.Create
  private val buffer = new EnergyStorage(cfg.buffer(), cfg.fePerTick() * 4, cfg.buffer(), 0)
  private val inputOnly = new IEnergyStorage {
    override def receiveEnergy(maxReceive: Int, simulate: Boolean): Int = buffer.receiveEnergy(maxReceive, simulate)
    override def extractEnergy(maxExtract: Int, simulate: Boolean): Int = 0
    override def getEnergyStored: Int = buffer.getEnergyStored
    override def getMaxEnergyStored: Int = buffer.getMaxEnergyStored
    override def canExtract: Boolean = false
    override def canReceive: Boolean = true
  }
  private var powered = false
  private var started = false

  def energyHandler: IEnergyStorage = inputOnly

  override def addBehaviours(behaviours: JList[BlockEntityBehaviour]): Unit = {}

  override def getGeneratedSpeed: Float =
    if (powered || started) cfg.speed().toFloat else 0.0F

  override def calculateAddedStressCapacity: Float = if (powered) cfg.stressCapacity().toFloat else 0.0F

  override def initialize(): Unit = {
    super.initialize()
    if (level != null && !level.isClientSide) {
      powered = buffer.getEnergyStored >= cfg.fePerTick()
      started = started || powered
      updateGeneratedRotation()
    }
  }

  override protected def write(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean): Unit = {
    super.write(tag, registries, clientPacket)
    tag.putBoolean("ag_powered", powered)
    tag.putBoolean("ag_started", started)
  }

  override protected def read(tag: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean): Unit = {
    super.read(tag, registries, clientPacket)
    powered = tag.getBoolean("ag_powered")
    started = tag.getBoolean("ag_started")
  }

  override def tick(): Unit = {
    super.tick()
    if (level != null && !level.isClientSide) {
      val cost = cfg.fePerTick()
      // Hysteresis: start only with a small reserve, but keep running until empty.
      val canRun =
        if (powered) buffer.extractEnergy(cost, true) >= cost
        else buffer.getEnergyStored >= cost * 2
      if (canRun) buffer.extractEnergy(cost, false)
      if (canRun != powered) {
        powered = canRun
        if (canRun && !started) {
          // First start: establish the source. On later power loss we keep the source
          // attached with zero stress capacity so Create doesn't tear down the network.
          started = true
          updateGeneratedRotation()
        } else if (hasNetwork) {
          notifyStressCapacityChange(calculateAddedStressCapacity())
        }
        setChanged()
      }
    }
  }
}
