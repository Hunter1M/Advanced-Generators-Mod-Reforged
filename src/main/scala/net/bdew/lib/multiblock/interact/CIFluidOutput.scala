package net.bdew.lib.multiblock.interact

import net.neoforged.neoforge.fluids.capability.IFluidHandler

trait CIFluidOutput extends CIOutputFaces {
  def fluidOutput: IFluidHandler
}
