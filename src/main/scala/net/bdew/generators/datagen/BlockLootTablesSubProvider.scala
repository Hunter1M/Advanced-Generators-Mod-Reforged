package net.bdew.generators.datagen

import net.bdew.generators.registries.Blocks
import net.bdew.lib.datagen.LootTableUtils
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.LootTable

import java.util.function.BiConsumer

class BlockLootTablesSubProvider extends LootTableSubProvider {
  // These blocks only exist when an optional mod is loaded, so their loot tables are
  // static files with neoforge:conditions instead of being generated unconditionally.
  private val conditionalBlocks = Set("eu_output_", "joules_output", "mj_output", "kinetic_converter", "electrodynamics_output")

  override def generate(consumer: BiConsumer[ResourceKey[LootTable], LootTable.Builder]): Unit = {
    Blocks.all.foreach(blockReg => {
      val path = blockReg.getId.getPath
      if (!conditionalBlocks.exists(path.startsWith)) {
        val block = blockReg.get()
        LootTableUtils.addBlockEntry(block, LootTableUtils.makeSimpleDropTable(block), consumer)
      }
    })
  }
}
