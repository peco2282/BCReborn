/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.io.Serializable;

public interface BCDataSerializable extends Serializable {
  CompoundTag serialize(HolderLookup.Provider provider);

  void deserialize(CompoundTag tag, HolderLookup.Provider provider);
}
