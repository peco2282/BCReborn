/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.saved;

import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.world.level.block.Block;

/**
 * Represents a blueprint template that can load and save data related to blueprint configurations.
 *
 * @author peco2282
 */
public class BluePrintTemplate {

  /**
   * Loads blueprint data from the given tag using the provided block holder getter.
   *
   * @param getter The block holder getter used to resolve blocks.
   * @param tag The data tag containing blueprint information to load.
   */
  public void load(HolderGetter<Block> getter, CompoundTag tag) {}

  /**
   * Saves the blueprint data into a {@link CompoundTag}.
   *
   * @param cache A range cache object used during the save operation.
   * @return A {@link CompoundTag} containing the saved blueprint data.
   */
  public CompoundTag save(RangeCache cache) {
    CompoundTag tag = new CompoundTag();
    tag.put("size", new Size(0, 0, 0).tag());
    return tag;
  }

  /** Represents the size of a blueprint with x, y, and z dimensions. */
  private record Size(int x, int y, int z) {

    /**
     * Converts the size dimensions into an {@link IntArrayTag}.
     *
     * @return An {@link IntArrayTag} containing the size dimensions [x, y, z].
     */
    IntArrayTag tag() {
      return new IntArrayTag(new int[] {x, y, z});
    }
  }
}
