package peco2282.bcreborn.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.lib.block.BlockMarkerBase;
import peco2282.bcreborn.lib.block.TileBaseNeptune;
import peco2282.bcreborn.utils.PropertyBuilder;

public class BlockMarkerPath extends BlockMarkerBase {
  public BlockMarkerPath(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder());
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<? extends TileBaseNeptune> codec() {
    return codecInstance(BlockMarkerPath::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

  }
}
