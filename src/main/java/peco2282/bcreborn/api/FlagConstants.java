/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface defining flag constants used for block updates in Minecraft.
 * These flags control various aspects of block state changes, including neighbor notifications,
 * client updates, rendering behavior, and special handling cases like piston movement.
 * The flags can be combined using bitwise operations to achieve multiple effects.
 *
 * @see Level#setBlock(BlockPos, BlockState, int)
 * @since 1.0
 */
@SuppressWarnings("PointlessBitwiseExpression")
public interface FlagConstants {
  /**
   * A flag indicating that a block update should notify neighboring blocks.
   * This is typically used in conjunction with other update flags to control
   * behavior when setting a block in the world.
   */
  int UPDATE_NEIGHBORS = 1 << 0;
  /**
   * A flag indicating that a block update should be sent to clients.
   * This ensures that the block state changes are propagated to the client-side
   * for rendering and other client-specific updates.
   */
  int UPDATE_CLIENTS = 1 << 1;
  /**
   * A flag indicating that a block update should not trigger a re-render.
   * This can be used to optimize performance when the visual representation
   * of the block remains unchanged, even if other updates are processed.
   */
  int UPDATE_NO_RERENDER = 1 << 2;
  /**
   * A flag indicating that the block's shape is already known and does not need to be recalculated.
   * This flag is typically used to optimize block updates by skipping redundant shape checks
   * when the block's geometry is not expected to change.
   */
  int UPDATE_KNOWN_SHAPE = 1 << 3;
  /**
   * A flag indicating that setting a block should suppress item drops that
   * would normally be generated as a result of the update. This is typically
   * used to prevent items from being dropped when replacing or modifying blocks
   * in specific contexts.
   */
  int UPDATE_SUPPRESS_DROPS = 1 << 4;
  /**
   * A flag indicating that the block update is caused by movement from a piston.
   * This is used to signal updates resulting from piston extensions or retractions,
   * ensuring that appropriate behaviors are handled for blocks involved in the move.
   */
  int UPDATE_MOVE_BY_PISTON = 1 << 5;

  /**
   * A flag indicating that all block update behaviors should be applied.
   * This combines all update flags to ensure the full set of block updates,
   * including notifications to neighboring blocks, client updates, suppression
   * of item drops, and any other specified behaviors. This is useful when a
   * comprehensive block update is required.
   */
  int UPDATE_ALL = 1 << 6;
}
