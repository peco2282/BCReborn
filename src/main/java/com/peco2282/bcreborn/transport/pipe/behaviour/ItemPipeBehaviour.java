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
package com.peco2282.bcreborn.transport.pipe.behaviour;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.transport.SpeedHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Behaviour specific to item pipes.
 */
public interface ItemPipeBehaviour extends PipeBehaviour {
  /**
   * Called when an item is injected into the pipe.
   */
  default void onInjectItem(PipeBlockEntity pipe, ItemStack stack, Direction from, float speed) {
  }

  /**
   * Called once when an item reaches the pipe centre (progress >= 0.5).
   * Used e.g. by the Void pipe; {@code stack.setCount(0)} makes ItemTransportModule remove the item.
   */
  default void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
  }

  /**
   * Adjusts the item speed once, when the item enters or bounces.
   */
  default void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    SpeedHelper.readjustSpeed(item, SpeedHelper.SLOWDOWN);
  }

  /**
   * Chooses the next direction (null uses the default routing).
   */
  @Nullable
  default Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    return null;
  }

  /**
   * Original {@code PipeEventItem.FindDest}: may remove entries from (or reduce) the default routing's candidate
   * sides before one is picked at random. If the list ends up empty the item bounces back.
   *
   * @param destinations the connected, open sides except the one the item came from (mutable)
   */
  default void filterDestinations(PipeBlockEntity pipe, TravelingItem item, List<Direction> destinations) {
  }

  /**
   * Whether the pipe accepts the item.
   */
  default boolean canAcceptItem(PipeBlockEntity pipe, ItemStack stack, Direction from) {
    return true;
  }
}
