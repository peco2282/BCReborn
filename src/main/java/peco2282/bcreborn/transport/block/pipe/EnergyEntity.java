/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class EnergyEntity implements Entity {
  @Override
  public CompoundTag serialize(HolderLookup.Provider registryAccess) {
    return new CompoundTag();
  }

  @Override
  public void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag) {}
}
