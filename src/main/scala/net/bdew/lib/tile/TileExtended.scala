package net.bdew.lib.tile

import net.bdew.lib.capabilities.CapabilityProvider
import net.bdew.lib.{Event, Event1}
import net.minecraft.core.{BlockPos, HolderLookup}
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.state.BlockState

class TileExtended(teType: BlockEntityType[_], pos: BlockPos, state: BlockState) extends BlockEntity(teType, pos, state) with CapabilityProvider {
  val persistSave: Event1[CompoundTag] = Event[CompoundTag]
  val persistLoad: Event1[CompoundTag] = Event[CompoundTag]
  val sendClientUpdate: Event1[CompoundTag] = Event[CompoundTag]
  val handleClientUpdate: Event1[CompoundTag] = Event[CompoundTag]

  override final def saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.saveAdditional(tag, registries)
    persistSave.trigger(tag)
  }

  override final def loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    super.loadAdditional(tag, registries)
    if (this.getLevel == null || !this.getLevel.isClientSide)
      persistLoad.trigger(tag)
  }

  override def handleUpdateTag(tag: CompoundTag, registries: HolderLookup.Provider): Unit = {
    handleClientUpdate.trigger(tag)
    super.handleUpdateTag(tag, registries)
  }

  def sendUpdateToClients(): Unit = {
    val state = level.getBlockState(worldPosition)
    level.sendBlockUpdated(worldPosition, state, state, 3)
  }

  override final def getUpdatePacket: ClientboundBlockEntityDataPacket = {
    if (sendClientUpdate.hasListeners) {
      ClientboundBlockEntityDataPacket.create(this)
    } else null
  }

  override def getUpdateTag(registries: HolderLookup.Provider): CompoundTag = {
    val tag = super.getUpdateTag(registries)
    sendClientUpdate.trigger(tag)
    tag
  }

  override def onDataPacket(net: Connection, pkt: ClientboundBlockEntityDataPacket, registries: HolderLookup.Provider): Unit = {
    if (pkt.getTag != null && !pkt.getTag.isEmpty)
      handleClientUpdate.trigger(pkt.getTag)
  }
}
