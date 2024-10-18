package peco2282.bcreborn.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.BCConfiguration;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.core.block.entity.BCCoreBlockEntityTypes;
import peco2282.bcreborn.core.block.entity.MarkerVolumeBlockEntity;
import peco2282.bcreborn.lib.block.BlockMarkerBase;
import peco2282.bcreborn.lib.block.TileBaseNeptune;
import peco2282.bcreborn.lib.block.entity.TileNeptune;
import peco2282.bcreborn.utils.PropertyBuilder;

public class BlockMarkerVolume extends BlockMarkerBase {

  private static final VoxelShape BOTTOM = box(7, 0, 7, 9, 9, 9);
  private static final VoxelShape UP = box(7, 7, 7, 9, 16, 9);

  public BlockMarkerVolume(Properties properties, @NotNull String id) {
    super(
        properties.lightLevel(s -> s.getValue(BCProperties.ACTIVE) ? 8 : 1).noCollission(),
        id,
        PropertyBuilder.builder().add(BCProperties.ACTIVE, false));
  }

  public static @Nullable BlockPos matchLine(Level level, BlockPos pos) {
    int max = BCConfiguration.maxVolumeLength;
    int curr;
    BlockPos currPos;

    // X
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.north(curr)).getBlock();
      if (block instanceof BlockMarkerVolume) return currPos;
    }

    // Y
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.above(curr)).getBlock();
      if (block instanceof BlockMarkerVolume) return currPos;
    }

    // Z
    for (curr = -max; curr <= max; curr++) {
      if (curr == 0) continue;
      Block block = level.getBlockState(currPos = pos.east(curr)).getBlock();
      if (block instanceof BlockMarkerVolume) return currPos;
    }
    return null;
  }

  @Override
  protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    return BOTTOM;
  }

  @Override
  public @NotNull TileNeptune newBlockEntity(BlockPos pos, BlockState state) {
    return new MarkerVolumeBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<? extends TileBaseNeptune> codec() {
    return codecInstance(BlockMarkerVolume::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return BaseEntityBlock.createTickerHelper(p_153214_, BCCoreBlockEntityTypes.MARKER_VOLUME.get(), MarkerVolumeBlockEntity::tick);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, BlockHitResult p_60508_) {
    if (!p_60504_.isClientSide) return super.useWithoutItem(p_60503_, p_60504_, p_60505_, p_60506_, p_60508_);

    BlockPos pos = matchLine(p_60504_, p_60505_);
    if (pos != null) {
      BlockEntity entity = p_60504_.getBlockEntity(pos);
      System.out.println("BlockMarkerVolume.useWithoutItem " + p_60505_ + " " + pos);
      if (entity instanceof MarkerVolumeBlockEntity volume) {
        BlockState activate = volume.disabled();
        p_60504_.setBlockAndUpdate(pos, activate);
      }
    }
    return super.useWithoutItem(p_60503_, p_60504_, p_60505_, p_60506_, p_60508_);
  }

  @Override
  protected boolean canSurvive(BlockState p_60525_, LevelReader p_60526_, BlockPos p_60527_) {
    return canSupportCenter(p_60526_, p_60527_.below(), Direction.UP);
  }
}
