package peco2282.bcreborn.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.BCConfiguration;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.core.MarkerPlaceHolder;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.block.MarkerVolumeBlock;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;

import java.util.*;
import java.util.stream.Stream;


public class MarkerVolumeBlockEntity extends NeptuneBlockEntity {
  public static final List<MarkerVolumeBlockEntity> rendered = new ArrayList<>();
  public MarkerVolumeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCCoreBlockEntityTypes.MARKER_VOLUME.get(), p_155229_, p_155230_);
  }
  private MarkerPlaceHolder holder;

  public static void tick(Level level, BlockPos pos, BlockState state, MarkerVolumeBlockEntity entity) {
  }

  private Stream<BlockPos> getCorners() {
    return BlockPos.betweenClosedStream(getBlockPos(), getBlockPos())
        .filter(p_272561_ -> this.level.getBlockState(p_272561_).is(BCCoreBlocks.MARKER_VOLUME.get()))
        .map(Objects.requireNonNull(this.level)::getBlockEntity)
        .filter(p_155802_ -> p_155802_ instanceof MarkerVolumeBlockEntity)
        .map(BlockEntity::getBlockPos);
  }

  public static MarkerPlaceHolder holder(Level level, BlockPos pos, MarkerVolumeBlockEntity volume) {
    int max = BCConfiguration.maxVolumeLength;
    int curr;
    BlockPos currPos;
    MarkerPlaceHolder holder = new MarkerPlaceHolder(pos);
    Set<MarkerVolumeBlockEntity> entities = search(level, pos, new HashSet<>());
    entities.forEach(entity -> holder.add(entity.getBlockPos()));
    entities.forEach(entity -> entity.holder = holder);
    volume.holder = holder;
    rendered.addAll(entities);
    rendered.remove(volume);
    return holder;
  }

  public boolean norender() {
    return rendered.contains(this);
  }

  public BlockState active() {
    return getBlockState().setValue(BCProperties.ACTIVE, true);
  }

  public boolean isActive() {
    return isSignal() || isConnected();
  }

  public boolean isSignal() {
    return getBlockState().getValue(BCProperties.ACTIVE);
  }

  public boolean isConnected() {
    return getBlockState().getValue(BCProperties.CONNECTED);
  }

  public BlockState disabled() {
    return getBlockState().setValue(BCProperties.ACTIVE, false);
  }

  public MarkerPlaceHolder renderer() {
    return holder(Objects.requireNonNull(getLevel()), getBlockPos(), this);
  }

  static Set<MarkerVolumeBlockEntity> search(Level level, BlockPos pos, Set<MarkerVolumeBlockEntity> gathered) {
    int max = BCConfiguration.maxVolumeLength;
    int curr;
    BlockPos currPos;
    // X
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.north(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        if (gathered.contains(entity)) continue;
        gathered.add(entity);
        gathered.addAll(search(level, currPos, gathered));
        break;
      }
    }

    // Y
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.above(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        if (gathered.contains(entity)) continue;
        gathered.add(entity);
        gathered.addAll(search(level, currPos, gathered));
        break;
      }
    }

    // Z
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.east(curr)).getBlock();
      if (block instanceof MarkerVolumeBlock && level.getBlockEntity(currPos) instanceof MarkerVolumeBlockEntity entity) {
        if (gathered.contains(entity)) continue;
        gathered.add(entity);
        gathered.addAll(search(level, currPos, gathered));
        break;
      }
    }
    return gathered;
  }
}
