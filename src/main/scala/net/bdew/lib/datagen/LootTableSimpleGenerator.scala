package net.bdew.lib.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.loot.{LootTableProvider, LootTableSubProvider}
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet

import java.util.concurrent.CompletableFuture

class LootTableSimpleGenerator(out: PackOutput, context: LootContextParamSet, factory: () => LootTableSubProvider, lookup: CompletableFuture[net.minecraft.core.HolderLookup.Provider]) extends
  LootTableProvider(out, java.util.Set.of(),
    java.util.List.of(new LootTableProvider.SubProviderEntry(_ => factory(), context)), lookup)
