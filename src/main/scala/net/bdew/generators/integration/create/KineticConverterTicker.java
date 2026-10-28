package net.bdew.generators.integration.create;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class KineticConverterTicker implements BlockEntityTicker<TileKineticConverter> {
    @Override
    public void tick(Level level, BlockPos pos, BlockState state, TileKineticConverter be) {
        be.tick();
    }
}
