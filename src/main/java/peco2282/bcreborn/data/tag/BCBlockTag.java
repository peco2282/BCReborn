/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data.tag;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.BCReborn;

/**
 * This class contains the tag definitions for blocks within the BC Reborn mod. Tags are used to
 * group blocks for specific purposes, such as engines, pipes, and package types.
 *
 * <p>Usage: - Use these tags to manage sets of blocks in recipes, datapacks, or in-game pipe.
 *
 * @author peco2282
 */
@SuppressWarnings("SameParameterValue")
public class BCBlockTag {

  // Type
  /** Represents the tag for all blocks classified as engines. */
  public static final TagKey<Block> ENGINE = create("engine");

  /** Represents the tag for all blocks classified as pipes. */
  public static final TagKey<Block> PIPE = create("pipe");

  /** Represents the tag for all blocks classified as item-related pipes. */
  public static final TagKey<Block> ITEM_PIPE = create("item_pipe");

  /** Represents the tag for all blocks classified as fluid-related pipes. */
  public static final TagKey<Block> FLUID_PIPE = create("fluid_pipe");

  /** Represents the tag for all blocks classified as energy-related pipes. */
  public static final TagKey<Block> ENERGY_PIPE = create("energy_pipe");

  // Package
  /** Represents the tag for blocks that are part of the "Core" package in the mod. */
  public static final TagKey<Block> CORE = create("core");

  /** Represents the tag for blocks that are part of the "Builder" package in the mod. */
  public static final TagKey<Block> BUILDER = create("builder");

  /** Represents the tag for blocks that are part of the "Transport" package in the mod. */
  public static final TagKey<Block> TRANSPORT = create("transport");

  /**
   * Creates a new block tag with the specified name.
   *
   * <p>This method is private and is used to initialize the public static final fields.
   *
   * @param name The name of the tag to create.
   * @return A TagKey instance for the specified block tag.
   */
  private static TagKey<Block> create(String name) {
    return BlockTags.create(BCReborn.MODID, name);
  }
}
