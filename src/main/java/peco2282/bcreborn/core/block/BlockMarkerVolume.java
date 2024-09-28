package peco2282.bcreborn.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.lib.block.BlockMarkerBase;
import peco2282.bcreborn.lib.block.entity.TileNeptune;

public class BlockMarkerVolume extends BlockMarkerBase {
  public BlockMarkerVolume(Properties properties, @NotNull String id) {
    super(properties, id);
  }

  @Override
  public @NotNull TileNeptune newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }
}
