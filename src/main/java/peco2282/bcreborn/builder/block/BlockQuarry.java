package peco2282.bcreborn.builder.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.builder.block.entity.QuarryBlockEntity;
import peco2282.bcreborn.lib.block.TileBaseNeptune;

public class BlockQuarry extends TileBaseNeptune implements RotatableFacing {
  public BlockQuarry(Properties properties, @NotNull String id) {
    super(properties, id);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new QuarryBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<? extends TileBaseNeptune> codec() {
    return codecInstance(BlockQuarry::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

  }
}
