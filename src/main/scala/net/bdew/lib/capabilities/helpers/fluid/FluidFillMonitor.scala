package net.bdew.lib.capabilities.helpers.fluid

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

/**
 * Wraps another fluid handler and calls the passed onFill function when fluid is filled
 */
class FluidFillMonitor(val base: IFluidHandler, onFill: FluidStack => Unit) extends FluidHandlerProxy {
  override def fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int = {
    val filled = super.fill(resource, action)
    if (filled > 0 && action.execute) {
      val tmp = resource.copy()
      tmp.setAmount(filled)
      onFill(tmp)
    }
    filled
  }
}
