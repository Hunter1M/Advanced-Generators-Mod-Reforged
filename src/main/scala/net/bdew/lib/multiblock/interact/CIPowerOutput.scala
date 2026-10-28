package net.bdew.lib.multiblock.interact

import net.neoforged.neoforge.energy.IEnergyStorage

trait CIPowerOutput extends CIOutputFaces {
  def powerOutput: IEnergyStorage
}
