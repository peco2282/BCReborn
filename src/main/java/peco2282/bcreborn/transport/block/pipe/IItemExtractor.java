/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

import io.netty.util.collection.IntObjectMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IItemExtractor extends PipeBlock {
  /**
   * Extracts an item from a neighboring container.
   *
   * @param direction The direction to extract from.
   * @return The extracted ItemStack.
   */
  IntObjectMap<ItemStack> extractItem(Direction direction);
}
