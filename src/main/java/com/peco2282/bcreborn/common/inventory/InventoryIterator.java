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
package com.peco2282.bcreborn.common.inventory;


import com.peco2282.bcreborn.api.core.IInvSlot;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;

public final class InventoryIterator {

  /**
   * Deactivate constructor
   */
  private InventoryIterator() {
  }

  public static Iterable<IInvSlot> getIterable(Container inv) {
    return getIterable(inv, Direction.UP);
  }

  /**
   * Returns an Iterable object for the specified side of the inventory.
   *
   * @param inv The inventory to iterate over.
   * @param side The side of the inventory to iterate over.
   * @return Iterable
   */
  public static Iterable<IInvSlot> getIterable(Container inv, Direction side) {
    if (inv instanceof WorldlyContainer) {
      return new InventoryIteratorSided((WorldlyContainer) inv, side);
    }

    return new InventoryIteratorSimple(inv);
  }

}
