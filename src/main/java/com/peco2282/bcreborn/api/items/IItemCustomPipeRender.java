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
package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface for items that require custom rendering when traveling through a pipe.
 */
public interface IItemCustomPipeRender {

  /**
   * Gets the scale at which the item should be rendered in the pipe.
   *
   * @param stack The item stack.
   * @return The rendering scale.
   */
  float getPipeRenderScale(ItemStack stack);

  /**
   * Performs custom rendering for the item in the pipe.
   *
   * @param stack The item stack.
   * @param x     The x-coordinate.
   * @param y     The y-coordinate.
   * @param z     The z-coordinate.
   * @return True if custom rendering was performed, false to use the default renderer.
   */
  @OnlyIn(Dist.CLIENT)
  boolean renderItemInPipe(ItemStack stack, double x, double y, double z);
}
