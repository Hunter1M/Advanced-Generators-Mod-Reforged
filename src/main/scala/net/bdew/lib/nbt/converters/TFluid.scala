package net.bdew.lib.nbt.converters

import net.bdew.lib.nbt.ConvertedType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.material.Fluid
import net.minecraft.core.registries.BuiltInRegistries

object TFluid extends ConvertedType[Fluid, String] {
  override def encode(v: Fluid): String = BuiltInRegistries.FLUID.getKey(v).toString
  override def decode(v: String): Option[Fluid] =
    Option(BuiltInRegistries.FLUID.get(ResourceLocation.parse(v)))
}
