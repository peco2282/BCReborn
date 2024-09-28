package peco2282.bcreborn.lib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import peco2282.bcreborn.core.block.BlockMarkerVolume;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;

import java.util.EnumMap;
import java.util.Map;

public abstract class BlockMarkerBase extends TileBaseNeptune {
  private static final Map<Direction, AABB> BOUNDING_BOXES = new EnumMap<>(Direction.class);
  public static final MapCodec<BlockMarkerVolume> CODEC = RecordCodecBuilder
      .mapCodec(instance -> instance.group(
          propertiesCodec(),
          Codec.STRING.fieldOf("id").forGetter(BlockBaseNeptune::getId)
      ).apply(instance, BlockMarkerVolume::new));

  static {
    double halfWidth = 0.1;
    double h = 0.65;
    // Little variables to make reading a *bit* more sane
    final double nw = 0.5 - halfWidth;
    final double pw = 0.5 + halfWidth;
    final double ih = 1 - h;
    BOUNDING_BOXES.put(Direction.DOWN, new AABB(nw, ih, nw, pw, 1, pw));
    BOUNDING_BOXES.put(Direction.UP, new AABB(nw, 0, nw, pw, h, pw));
    BOUNDING_BOXES.put(Direction.SOUTH, new AABB(nw, nw, 0, pw, pw, h));
    BOUNDING_BOXES.put(Direction.NORTH, new AABB(nw, nw, ih, pw, pw, 1));
    BOUNDING_BOXES.put(Direction.EAST, new AABB(0, nw, nw, h, pw, pw));
    BOUNDING_BOXES.put(Direction.WEST, new AABB(ih, nw, nw, 1, pw, pw));
  }

  public <T extends Comparable<T>, P extends Property<T>> BlockMarkerBase(Properties properties, @NotNull String id) {
    super(properties.destroyTime(.25F), id,
        new Tuple<>(BCProperties.BLOCK_FACING_6, Direction.UP),
        new Tuple<>(BCProperties.ACTIVE, false)
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    super.createBlockStateDefinition(p_49915_);
    p_49915_.add(BCProperties.BLOCK_FACING_6, BCProperties.ACTIVE);
  }

  @Override
  public @NotNull BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
    return defaultBlockState().setValue(BCProperties.BLOCK_FACING_6, p_49820_.getClickedFace());
  }

  @Override
  protected void neighborChanged(BlockState p_60509_, Level p_60510_, BlockPos p_60511_, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
    if (p_60509_.getBlock() != this) return;
    p_60510_.destroyBlock(p_60511_, true);
  }

  @Override
  protected final @NotNull MapCodec<? extends Block> codec() {
    return CODEC;
  }
}
