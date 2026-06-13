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

import net.minecraft.world.item.ItemStack;

/**
 * Interface for objects that can provide and fulfill item requests.
 */
public interface IRequestProvider {
  /**
   * Returns the number of requests available.
   *
   * @return The request count.
   */
  int getRequestsCount();

  /**
   * Returns the requested item in the specified slot.
   *
   * @param slot The slot index.
   * @return The requested item stack.
   */
  ItemStack getRequest(int slot);

  /**
   * Offers an item to fulfill a request in the specified slot.
   *
   * @param slot  The slot index.
   * @param stack The item stack being offered.
   * @return The remaining item stack after the offer is processed.
   */
  ItemStack offerItem(int slot, ItemStack stack);
}
