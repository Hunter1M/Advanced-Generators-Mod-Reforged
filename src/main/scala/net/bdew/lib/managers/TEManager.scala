package net.bdew.lib.managers

import net.bdew.lib.capabilities.CapabilityRegistry
import net.bdew.lib.registries.RegistryObject
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.{BlockEntity, BlockEntityType}
import net.minecraft.world.level.block.state.BlockState

class TEManager extends RegistryManager(BuiltInRegistries.BLOCK_ENTITY_TYPE) {
  def registerWithBlock[E <: BlockEntity](name: String, teFactory: (BlockEntityType[E], BlockPos, BlockState) => E, blockReg: RegistryObject[_ <: Block]): RegistryObject[BlockEntityType[E]] = {
    var teType: RegistryObject[BlockEntityType[E]] = null // for forward reference to pass to factory
    teType =
      register(name, () => {
        val block = blockReg.get()
        val builder = BlockEntityType.Builder.of[E]((pos: BlockPos, state: BlockState) => teFactory(teType.get(), pos, state), block)
        val tmp = builder.build(null)
        TEManager.synchronized {
          // this will be called in parallel from different mods so needs to synchronize access
          TEManager.blockMap += (block -> tmp)
        }
        CapabilityRegistry.addType(tmp)
        tmp
      })
    teType
  }
}

object TEManager {
  var blockMap = Map.empty[Block, BlockEntityType[_]]
}
