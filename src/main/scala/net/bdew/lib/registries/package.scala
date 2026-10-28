package net.bdew.lib

import net.neoforged.neoforge.registries.DeferredHolder

package object registries {
  /**
   * Compatibility alias for Forge's RegistryObject.
   *
   * NeoForge uses DeferredHolder with two type parameters (the registry entry type
   * and the concrete value type). bdlib only ever needs the value type, so the
   * registry parameter is hidden behind an existential.
   */
  type RegistryObject[T] = DeferredHolder[_ <: AnyRef, T]
}
