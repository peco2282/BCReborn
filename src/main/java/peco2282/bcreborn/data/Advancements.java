/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data;

import net.minecraft.advancements.AdvancementHolder;
import peco2282.bcreborn.annotation.LateinitField;
import peco2282.bcreborn.utils.OptionalWith;

/**
 * A class that holds static references to advancements in the system. These advancements are lazily
 * initialized using the {@link OptionalWith} utility.
 *
 * @author peco2282
 */
public class Advancements {
  /**
   * Represents the root advancement, which serves as the starting node in the game's advancement
   * progression tree.
   */
  @LateinitField public static final OptionalWith<AdvancementHolder> ROOT = OptionalWith.empty();

  @LateinitField public static final OptionalWith<AdvancementHolder> BUILDER = OptionalWith.empty();
}
