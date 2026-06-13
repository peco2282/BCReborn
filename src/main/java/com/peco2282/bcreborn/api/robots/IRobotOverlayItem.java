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
package com.peco2282.bcreborn.api.robots;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Interface for items that can provide an overlay when rendered on a robot.
 */
public interface IRobotOverlayItem {
  /**
   * Checks if the given item stack is a valid overlay for a robot.
   *
   * @param stack The item stack.
   * @return {@code true} if valid, {@code false} otherwise.
   */
  boolean isValidRobotOverlay(ItemStack stack);

  /**
   * Renders the robot overlay.
   *
   * @param stack          The item stack.
   * @param textureManager The texture manager.
   */
  @OnlyIn(Dist.CLIENT)
  void renderRobotOverlay(ItemStack stack, TextureManager textureManager);
}
