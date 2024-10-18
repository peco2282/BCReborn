package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.lib.block.entity.TileNeptune;
import peco2282.bcreborn.utils.OptionalWith;

import java.util.Objects;


public class MarkerVolumeBlockEntity extends TileNeptune {
  private final OptionalWith<BlockPos> FROM = OptionalWith.empty();
  private final OptionalWith<BlockPos> TO = OptionalWith.empty();

  public MarkerVolumeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCCoreBlockEntityTypes.MARKER_VOLUME.get(), p_155229_, p_155230_);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, MarkerVolumeBlockEntity entity) {
    entity.update(level, pos, state);
  }

  public BlockState active() {
    return getBlockState().setValue(BCProperties.ACTIVE, true);
  }

  public boolean isActive() {
    return getBlockState().getValue(BCProperties.ACTIVE);
  }

  public BlockState disabled() {
    return getBlockState().setValue(BCProperties.ACTIVE, false);
  }

  public void render(Level level, BlockPos startPos, BlockPos endPos) {
    FROM.ifAbsentSet(startPos);
    TO.ifAbsentSet(endPos);
  }

  public boolean isEach(MarkerVolumeBlockEntity target) {
    if (FROM.isPresent() && TO.isPresent() && target.FROM.isPresent() && target.TO.isPresent()) {
      BlockPos tFrom = target.FROM.get();
      BlockPos tTo = target.TO.get();

      if (Objects.equals(FROM.get(), tFrom) && Objects.equals(TO.get(), tTo)) {
        return true;
      }
      return Objects.equals(FROM.get(), tTo) && Objects.equals(TO.get(), tFrom);
    }
    return false;
  }

  private void update(Level level, BlockPos pos, BlockState state) {
  }
}
