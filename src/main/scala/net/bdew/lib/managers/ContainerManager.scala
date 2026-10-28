package net.bdew.lib.managers

import net.bdew.lib.registries.RegistryObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.{AbstractContainerMenu, MenuType}
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension


abstract class ContainerManager extends RegistryManager(BuiltInRegistries.MENU) {
  def registerSimple[T <: AbstractContainerMenu](id: String)(factory: (Int, Inventory, FriendlyByteBuf) => T): RegistryObject[MenuType[T]] = {
    super.register(id, () => IMenuTypeExtension.create((id, inv, data) => factory(id, inv, data)))
  }

  def registerPositional[E <: BlockEntity, T <: AbstractContainerMenu](id: String, te: RegistryObject[BlockEntityType[E]])(factory: (Int, Inventory, E) => T): RegistryObject[MenuType[T]] = {
    registerSimple(id) { (id, inv, data) =>
      factory(id, inv, te.get().getBlockEntity(inv.player.level, data.readBlockPos()))
    }
  }

  override def init(): Unit = {
    super.init()
  }
}
