/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.api.Debuggable;
import peco2282.bcreborn.lib.block.BCBaseBlock;
import peco2282.bcreborn.lib.block.BCBaseEntityBlock;

/**
 * Base interface for BCReborn mod's block.
 *
 * @see BCBaseBlock
 * @see BCBaseEntityBlock
 * @author peco2282
 */
public interface BCBlock extends Debuggable {
  String getId();

  default Block getBlock() {
    return (Block) this;
  }

  default Item.Properties itemProperties() {
    return new Item.Properties();
  }
}
