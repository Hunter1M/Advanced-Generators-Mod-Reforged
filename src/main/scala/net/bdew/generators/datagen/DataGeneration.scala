package net.bdew.generators.datagen

import net.bdew.lib.datagen.LootTableSimpleGenerator
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.neoforge.data.event.GatherDataEvent

object DataGeneration {
  def onGatherData(ev: GatherDataEvent): Unit = {
    val dataGenerator = ev.getGenerator
    val efh = ev.getExistingFileHelper
    dataGenerator.addProvider(ev.includeServer, new LootTableSimpleGenerator(dataGenerator.getPackOutput, LootContextParamSets.BLOCK, () => new BlockLootTablesSubProvider, ev.getLookupProvider))
    dataGenerator.addProvider(ev.includeServer, new BlockStates(dataGenerator.getPackOutput, efh))
  }
}
