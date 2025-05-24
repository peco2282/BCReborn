/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.lib.block;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.block.Facing;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.utils.PropertyBuilder;

/**
 * BCBaseBlock serves as a foundational block class within the Neptune-themed mod. It provides
 * default configuration for block properties, including durability, explosion resistance, and sound
 * type. This class is meant to be extended by specific block implementations which define their own
 * state properties.
 *
 * @author peco2282
 */
public abstract class BCBaseBlock extends Block implements BCBlock {
  private static final Logger log = LoggerFactory.getLogger(BCBaseBlock.class);
  protected final String id;

  /**
   * Constructs a BCBaseBlock with the given properties, identifier, and property builder.
   *
   * @param properties The block properties to be applied, such as hardness and resistance.
   * @param id The unique identifier for this block.
   * @param builder A helper to create and configure block states.
   */
  public BCBaseBlock(Properties properties, @NotNull String id, PropertyBuilder builder) {
    super(update(properties));
    BlockState raw = getStateDefinition().any();
    registerDefaultState(builder.set(raw));
    this.id = id;
  }

  private static @NotNull Properties update(@NotNull Properties properties) {
    return properties.destroyTime(5.0F).explosionResistance(10.0F).sound(SoundType.METAL);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    gatherStateDefinition(p_49915_);
  }

  protected abstract void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

  /**
   * Retrieves the unique identifier associated with this block.
   *
   * @return The unique block ID as a String.
   */
  public String getId() {
    return id;
  }

  /**
   * Rotates the block state based on the given rotation. Supports blocks implementing
   * RotatableFacing.
   *
   * @param state The current block state.
   * @param rotation The rotation to apply.
   * @return The rotated block state or the original state if not rotatable.
   */
  @Override
  protected BlockState rotate(BlockState state, Rotation rotation) {
    if (this instanceof RotatableFacing facing) {
      Property<Direction> prop = facing.getFacingProperty();
      return state.setValue(prop, rotation.rotate(state.getValue(prop)));
    }
    return state;
  }

  /**
   * Mirrors the block state based on the given mirror transformation. Supports blocks implementing
   * RotatableFacing.
   *
   * @param p_60528_ The current block state.
   * @param p_60529_ The mirroring transformation to apply.
   * @return The mirrored block state or the original state if not mirroring is applicable.
   */
  @Override
  protected BlockState mirror(BlockState p_60528_, Mirror p_60529_) {
    if (this instanceof RotatableFacing facing) {
      Property<Direction> prop = facing.getFacingProperty();
      return p_60528_.rotate(p_60529_.getRotation(p_60528_.getValue(prop)));
    }
    return p_60528_;
  }

  /**
   * Determines the initial block state for placement based on player orientation and surrounding
   * context.
   *
   * @param p_49820_ Contextual information about the placement, such as player orientation and
   *     position.
   * @return The block state to be set at the placement position.
   */
  @NotNull
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
    BlockState state = super.defaultBlockState();
    if (this instanceof Facing facing) {
      Direction orientation = p_49820_.getHorizontalDirection();
      if (Mth.abs((float) p_49820_.getPlayer().getX() - p_49820_.getClickedPos().getX()) < 2F
          && Mth.abs((float) p_49820_.getPlayer().getZ() - p_49820_.getClickedPos().getZ()) < 2F) {
        double y = p_49820_.getPlayer().getY() + p_49820_.getPlayer().getEyeHeight();

        if (y - p_49820_.getClickedPos().getY() > 2.0D) {
          orientation = Direction.DOWN;
        }
        if (p_49820_.getClickedPos().getY() - y > 0.0D) {
          orientation = Direction.UP;
        }
      } else {
        orientation = orientation.getOpposite();
      }
      return state.setValue(
          facing.getFacingProperty(),
          p_49820_.getNearestLookingDirection().getOpposite().getOpposite());
    }
    return state;
  }
}
