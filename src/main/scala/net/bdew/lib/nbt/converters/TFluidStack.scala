package net.bdew.lib.nbt.converters

import net.bdew.lib.nbt.ConvertedType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.{CompoundTag, Tag}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack

object TFluidStack extends ConvertedType[FluidStack, CompoundTag] {
  override def encode(v: FluidStack): CompoundTag = {
    val tag = new CompoundTag()
    tag.putString("Fluid", BuiltInRegistries.FLUID.getKey(v.getFluid).toString)
    tag.putInt("Amount", v.getAmount)
    tag
  }

  override def decode(v: CompoundTag): Option[FluidStack] = {
    if (!v.contains("Fluid", Tag.TAG_STRING)) return None
    val fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(v.getString("Fluid")))
    if (fluid == null || fluid == Fluids.EMPTY) None
    else Some(new FluidStack(fluid, v.getInt("Amount")))
  }
}
