package net.bdew.lib.data

import net.bdew.lib.data.base.{DataSlot, DataSlotContainer, UpdateKind}
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.fluids.{FluidStack, IFluidTank}

abstract class DataSlotTankBase(sz: Int) extends FluidTank(sz) with IFluidTank with IFluidHandler with DataSlot {
  updateKind = Set(UpdateKind.GUI, UpdateKind.SAVE)

  val sendCapacityOnUpdateKind = Set(UpdateKind.GUI)

  def save(t: CompoundTag, k: UpdateKind.Value): Unit = {
    val tag = new CompoundTag()
    tag.put("Fluid", net.bdew.lib.nbt.converters.TFluidStack.encode(getFluid))
    if (sendCapacityOnUpdateKind.contains(k))
      tag.putInt("size", getCapacity)
    t.put(name, tag)
  }

  def load(t: CompoundTag, k: UpdateKind.Value): Unit = {
    setFluid(FluidStack.EMPTY)
    val tag = t.getCompound(name)
    setFluid(net.bdew.lib.nbt.converters.TFluidStack.decode(tag.getCompound("Fluid")).getOrElse(FluidStack.EMPTY))
    if (sendCapacityOnUpdateKind.contains(k))
      setCapacity(tag.getInt("size"))
  }

  override def setFluid(fluid: FluidStack): Unit = execWithChangeNotify(super.setFluid(fluid))
  override def setCapacity(capacity: Int): FluidTank = execWithChangeNotify(super.setCapacity(capacity))

  override def onContentsChanged(): Unit = parent.dataSlotChanged(this)
}

case class DataSlotTank(name: String, parent: DataSlotContainer, size: Int) extends DataSlotTankBase(size) {
}

case class DataSlotTankRestricted(name: String, parent: DataSlotContainer, var size: Int, filterFluid: Fluid) extends DataSlotTankBase(size) {
  override def isFluidValid(stack: FluidStack): Boolean = stack.getFluid == filterFluid
}
