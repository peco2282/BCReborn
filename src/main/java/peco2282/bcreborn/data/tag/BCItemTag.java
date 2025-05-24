/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data.tag;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import peco2282.bcreborn.BCReborn;

/**
 * This class defines custom item tags for the BC Reborn mod.
 *
 * <p>It provides static references to commonly used item tags, such as gears.
 *
 * @author peco2282
 */
@SuppressWarnings("SameParameterValue")
public class BCItemTag {

  /** Tag for gear items. */
  public static final TagKey<Item> GEAR = create("gear");

  /**
   * Creates a {@link TagKey} for a given item tag name.
   *
   * @param name The name of the item tag.
   * @return The created {@link TagKey}.
   */
  private static TagKey<Item> create(String name) {
    return ItemTags.create(BCReborn.MODID, name);
  }
}
