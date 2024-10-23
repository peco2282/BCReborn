package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.BCConfiguration;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.core.MarkerPlaceHolder;
import peco2282.bcreborn.core.block.MarkerVolumeBlock;
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
  }

  public static MarkerPlaceHolder holder(Level level, BlockPos pos, MarkerVolumeBlockEntity volume) {
    int max = BCConfiguration.maxVolumeLength;
    int curr;
    BlockPos currPos;
    MarkerPlaceHolder holder = new MarkerPlaceHolder(pos);

    // X
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.north(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity) {
        holder.add(currPos);
        break;
      }
    }

    // Y
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.above(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity) {
        holder.add(currPos);
        break;
      }
    }

    // Z
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.east(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity) {
        holder.add(currPos);
        break;
      }
    }
    return holder;
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

  public MarkerPlaceHolder renderer() {
    return holder(Objects.requireNonNull(getLevel()), getBlockPos(), this);
  }
}
