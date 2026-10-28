package net.bdew.lib.managers

import net.bdew.lib.{BdLib, Misc}
import net.bdew.lib.registries.RegistryObject
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredRegister

class RegistryManager[T <: AnyRef](registry: DeferredRegister[T]) {
  def this(mcRegistry: Registry[T]) = this(DeferredRegister.create(mcRegistry, Misc.getActiveModId))
  def this(registryKey: ResourceKey[_ <: Registry[T]]) = this(DeferredRegister.create(registryKey, Misc.getActiveModId))

  var all = Set.empty[RegistryObject[_ <: T]]

  def init(): Unit = {
    registry.register(BdLib.modBus)
  }

  def register[R <: T](id: String, supplier: () => R): RegistryObject[R] = {
    val tmp: RegistryObject[R] = registry.register(id, new java.util.function.Supplier[R] {
      override def get(): R = supplier()
    })
    all = all + tmp
    tmp
  }
}
