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

public interface IItemTransporter extends PipeBlock {
  /**
   * Transports an item to the next pipe or container.
   *
   * @param stack The ItemStack to transport.
   * @param direction The direction the item came from.
   */
  void transportItem(IntObjectMap<ItemStack> stack, Direction direction);
}
