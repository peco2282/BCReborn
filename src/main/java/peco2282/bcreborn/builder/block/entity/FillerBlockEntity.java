package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.api.mj.MJHolder;
import peco2282.bcreborn.lib.block.entity.TileNeptune;

public class FillerBlockEntity extends TileNeptune {

  private static final long capacity = 16000 * 1_000_000L;
  private EnumFillerType type = EnumFillerType.NONE;
  private final MJHolder HOLDER = new MJHolder(capacity);

  public FillerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.FILLER.get(), p_155229_, p_155230_);
  }

  public EnumFillerType getFillerType() {
    return type;
  }

  private void update(Level level, BlockPos pos, BlockState state) {
    type = state.getValue(BCProperties.FILLER_TYPE);
    type.run();
  }

  public static void tick(Level level, BlockPos pos, BlockState state, FillerBlockEntity entity) {
    entity.update(level, pos, state);
  }
}
