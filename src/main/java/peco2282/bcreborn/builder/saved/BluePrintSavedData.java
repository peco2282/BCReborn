/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.saved;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.event.SaveDataEvent;

/**
 * Represents the saved data for a blueprint with handling for range and cache. Includes
 * functionality for saving, loading, and event handling.
 *
 * @author peco2282
 */
public class BluePrintSavedData extends SavedData {
  private final RangeCache cache;

  /**
   * Initializes the saved data with a specific start and end position.
   *
   * @param start The starting position of the blueprint.
   * @param end The ending position of the blueprint.
   */
  public BluePrintSavedData(BlockPos start, BlockPos end) {
    this(new RangeCache(start, end, System.currentTimeMillis()));
  }

  /**
   * Creates an instance of saved data using an existing range cache.
   *
   * @param cache The range cache to associate with this saved data.
   */
  public BluePrintSavedData(RangeCache cache) {
    this.cache = cache;
  }

  /** Default constructor, initializes the saved data with default starting and ending positions. */
  public BluePrintSavedData() {
    this(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1));
  }

  /**
   * Provides a factory for creating instances of {@link BluePrintSavedData}.
   *
   * @return A factory for creating and loading blueprint saved data.
   */
  public static Factory<BluePrintSavedData> factory() {
    return new Factory<>(BluePrintSavedData::new, BluePrintSavedData::load, DataFixTypes.STRUCTURE);
  }

  /**
   * Loads blueprint saved data from a compound tag.
   *
   * @param tag The CompoundTag containing the saved data.
   * @param provider The holder lookup provider.
   * @return A new instance of {@link BluePrintSavedData}.
   */
  private static BluePrintSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
    return new BluePrintSavedData(RangeCache.load(tag, provider));
  }

  /**
   * Saves the blueprint data into a compound tag and posts a save event.
   *
   * @param p_77763_ The CompoundTag to save data into.
   * @param p_334349_ The holder lookup provider.
   * @return The CompoundTag containing saved data.
   */
  @Override
  public CompoundTag save(CompoundTag p_77763_, HolderLookup.Provider p_334349_) {
    CompoundTag tag = cache.save(p_77763_);

    SaveDataEvent event = new SaveDataEvent(cache);
    BCReborn.EVENT_BUS.post(event);
    return tag;
  }

  /**
   * Retrieves the range cache associated with this saved data.
   *
   * @return The {@link RangeCache} instance.
   */
  public RangeCache getCache() {
    return cache;
  }
}
