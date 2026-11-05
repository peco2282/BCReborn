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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;

/**
 * Port of the original {@code PipeItemsClay}: items prefer an inventory over the next pipe.
 */
public class ClayItemPipeBehaviour implements ItemPipeBehaviour {

  public static final ClayItemPipeBehaviour INSTANCE = new ClayItemPipeBehaviour();

  private ClayItemPipeBehaviour() {
  }

  /** Original {@code transport.allowBouncing = true}. */
  @Override
  public boolean allowsBouncing() {
    return true;
  }

  /**
   * Original {@code eventHandler(PipeEventItem.FindDest)}: if any candidate side is not a pipe (an inventory that can
   * take the item), only those sides are kept; otherwise the pipes are kept.
   */
  @Override
  public void filterDestinations(PipeBlockEntity pipe, TravelingItem item, List<Direction> destinations) {
    List<Direction> nonPipes = new ArrayList<>();
    for (Direction dir : destinations) {
      if (!(pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir)) instanceof PipeBlockEntity)) {
        nonPipes.add(dir);
      }
    }
    if (!nonPipes.isEmpty()) {
      destinations.retainAll(nonPipes);
    }
  }
}
