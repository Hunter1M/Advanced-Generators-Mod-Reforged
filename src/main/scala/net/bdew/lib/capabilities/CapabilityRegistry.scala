package net.bdew.lib.capabilities

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.neoforged.neoforge.capabilities.{Capabilities, ICapabilityProvider, RegisterCapabilitiesEvent}
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler

import scala.collection.mutable

object CapabilityRegistry {
  private val types = mutable.Set.empty[BlockEntityType[_ <: BlockEntity]]

  def addType(teType: BlockEntityType[_ <: BlockEntity]): Unit = types += teType

  def registerCapabilities(event: RegisterCapabilitiesEvent): Unit = {
    for (rawType <- types) {
      val teType = rawType.asInstanceOf[BlockEntityType[BlockEntity]]

      event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, teType, new ICapabilityProvider[BlockEntity, Direction, IItemHandler] {
        override def getCapability(be: BlockEntity, side: Direction): IItemHandler = be match {
          case p: CapabilityProvider => p.itemHandler(side)
          case _ => null
        }
      })

      event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, teType, new ICapabilityProvider[BlockEntity, Direction, IFluidHandler] {
        override def getCapability(be: BlockEntity, side: Direction): IFluidHandler = be match {
          case p: CapabilityProvider => p.fluidHandler(side)
          case _ => null
        }
      })

      event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, teType, new ICapabilityProvider[BlockEntity, Direction, IEnergyStorage] {
        override def getCapability(be: BlockEntity, side: Direction): IEnergyStorage = be match {
          case p: CapabilityProvider => p.energyHandler(side)
          case _ => null
        }
      })
    }
  }
}
