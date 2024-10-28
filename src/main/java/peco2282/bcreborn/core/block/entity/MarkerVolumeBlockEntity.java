package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.BCConfiguration;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.core.MarkerPlaceHolder;
import peco2282.bcreborn.core.block.MarkerVolumeBlock;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MarkerVolumeBlockEntity extends NeptuneBlockEntity {
  public static final List<MarkerVolumeBlockEntity> rendered = new ArrayList<>();
  private final MarkerVolumeBlockEntity[] connections = new MarkerVolumeBlockEntity[3];
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
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        holder.add(currPos);
        rendered.add(entity);
        volume.connections[0] = entity;
        entity.connections[0] = volume;
        break;
      }
    }

    // Y
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.above(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        holder.add(currPos);
        rendered.add(entity);
        volume.connections[1] = entity;
        entity.connections[1] = volume;
        break;
      }
    }

    // Z
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.east(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        holder.add(currPos);
        rendered.add(entity);
        volume.connections[2] = entity;
        entity.connections[2] = volume;
        break;
      }
    }
    LOGGER.info(String.valueOf(holder));
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
