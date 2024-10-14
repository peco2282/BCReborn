package peco2282.bcreborn.lib.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.utils.PropertyBuilder;

import java.util.EnumMap;
import java.util.Map;

public abstract class BlockMarkerBase extends TileBaseNeptune {
  private static final Map<Direction, AABB> BOUNDING_BOXES = new EnumMap<>(Direction.class);

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

  public BlockMarkerBase(Properties properties, @NotNull String id, PropertyBuilder builder) {
    super(properties.destroyTime(.25F), id,
  builder.add(
      BCProperties.BLOCK_FACING_6, Direction.UP).add(
        BCProperties.ACTIVE, false)
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
}
