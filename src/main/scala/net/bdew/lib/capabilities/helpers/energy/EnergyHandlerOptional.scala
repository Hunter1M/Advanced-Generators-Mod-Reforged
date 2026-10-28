package net.bdew.lib.capabilities.helpers.energy

import net.neoforged.neoforge.energy.IEnergyStorage

class EnergyHandlerOptional(handler: () => Option[IEnergyStorage]) extends EnergyHandlerProxy {
  override def base: IEnergyStorage = handler().getOrElse(EnergyHandlerNull)
}

object EnergyHandlerOptional {
  def create(base: () => Option[IEnergyStorage]): IEnergyStorage = new EnergyHandlerOptional(base)
}
