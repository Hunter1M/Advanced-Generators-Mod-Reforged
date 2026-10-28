package net.bdew.lib.multiblock.interact

import net.neoforged.neoforge.energy.IEnergyStorage

trait CIPowerInput extends CIOutputFaces {
  def powerInput: IEnergyStorage
}
