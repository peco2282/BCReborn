/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

/**
 * Interface to be implemented by items that can be attached to pipes as pluggables.
 */
public interface IPipePluggableItem {

  /**
   * Creates a new {@link PipePluggable} instance from the item stack.
   *
   * @param pipe  The pipe it will be attached to.
   * @param side  The side it will be attached to.
   * @param stack The item stack.
   * @return A new {@link PipePluggable}.
   */
  PipePluggable<?> createPipePluggable(IPipe pipe, Direction side, ItemStack stack);
}
