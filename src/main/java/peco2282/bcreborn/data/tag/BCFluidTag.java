/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data.tag;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import peco2282.bcreborn.BCReborn;

/**
 * This class defines custom fluid tags for the BC Reborn mod.
 *
 * <p>It provides static references to commonly used fluid tags, such as burnable, burnable sources,
 * and flowing burnable fluids.
 *
 * @author peco2282
 */
public class BCFluidTag {

  /** Tag for burnable fluids. */
  public static final TagKey<Fluid> BURNABLE = create("burnable");

  /** Tag for burnable fluid sources. */
  public static final TagKey<Fluid> BURNABLE_SOURCE = create("burnable_source");

  /** Tag for burnable flowing fluids. */
  public static final TagKey<Fluid> BURNABLE_FLOWING = create("burnable_flowing");

  /**
   * Creates a {@link TagKey} for a given fluid tag key.
   *
   * @param key The name of the fluid tag.
   * @return The created {@link TagKey}.
   */
  private static TagKey<Fluid> create(String key) {
    return FluidTags.create(BCReborn.location(key));
  }
}
