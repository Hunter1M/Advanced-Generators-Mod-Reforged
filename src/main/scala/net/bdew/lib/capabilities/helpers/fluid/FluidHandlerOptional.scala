package net.bdew.lib.capabilities.helpers.fluid

import net.neoforged.neoforge.fluids.capability.IFluidHandler

class FluidHandlerOptional(handler: () => Option[IFluidHandler]) extends FluidHandlerProxy {
  override def base: IFluidHandler = handler().getOrElse(FluidHandlerNull)
}

object FluidHandlerOptional {
  def create(base: () => Option[IFluidHandler]): IFluidHandler = new FluidHandlerOptional(base)
}
