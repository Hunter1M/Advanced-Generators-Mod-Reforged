package net.bdew.lib.capabilities

import net.minecraft.core.Direction
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler

/**
 * Implemented by block entities that expose capabilities to their neighbours.
 *
 * NeoForge no longer has ICapabilityProvider; capabilities are registered per
 * block entity type in RegisterCapabilitiesEvent. CapabilityRegistry wires this
 * trait up for every block entity type bdlib knows about.
 */
trait CapabilityProvider {
  def itemHandler(side: Direction): IItemHandler = null
  def fluidHandler(side: Direction): IFluidHandler = null
  def energyHandler(side: Direction): IEnergyStorage = null
}
