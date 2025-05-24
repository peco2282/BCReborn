/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability.mj;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import peco2282.bcreborn.api.block.BCBlock;

@AutoRegisterCapability
public interface MJReceiver extends MJCapability {
  long require();

  <B extends Block & BCBlock> B receivedBy();
}
