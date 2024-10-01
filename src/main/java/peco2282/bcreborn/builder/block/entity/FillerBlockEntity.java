package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.TileNeptune;

public class FillerBlockEntity extends TileNeptune {

  private static final FillerType type = FillerType.FILL;

  public FillerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.FILLER.get(), p_155229_, p_155230_);
  }

  enum FillerType {
    FILL
  }
}
