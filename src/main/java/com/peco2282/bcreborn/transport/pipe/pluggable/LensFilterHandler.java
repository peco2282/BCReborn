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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of the original {@code LensFilterHandler} ({@code PipeEventItem.FindDest}): sides behind a filter only take
 * items of the filter's colour, and an item that matches a filter prefers those sides over unfiltered ones.
 */
public final class LensFilterHandler {

  private LensFilterHandler() {
  }

  /**
   * Reduces {@code destinations} to the sides the item may take: the sides whose filter matches the item's colour
   * if there are any, otherwise the sides without a filter.
   * <p>
   * A side is filtered by a filter on this pipe, or by a filter on the facing side of the next pipe. Two filters
   * of different colours close the side. A lens on this pipe in front of a filter on the next pipe closes the side
   * too, unless both have the same colour: then the lens paints the item as it leaves and the side counts as
   * unfiltered.
   */
  public static void filterDestinations(PipeBlockEntity pipe, TravelingItem item, List<Direction> destinations) {
    if (destinations.isEmpty()) return;
    DyeColor itemColor = item.getColor();
    List<Direction> matching = new ArrayList<>();
    List<Direction> unfiltered = new ArrayList<>();
    Level level = pipe.getLevel();

    for (Direction dir : destinations) {
      boolean hasFilter = false;
      boolean hasLens = false;
      DyeColor sideColor = null;

      // (1/2) This pipe's side.
      if (pipe.getPipePluggable(dir) instanceof LensPluggable own) {
        sideColor = own.getColor();
        if (own.isFilter()) {
          hasFilter = true;
        } else {
          hasLens = true;
        }
      }

      // (2/2) The facing side of the next pipe.
      if (level != null && level.isLoaded(pipe.getBlockPos().relative(dir))
        && level.getBlockEntity(pipe.getBlockPos().relative(dir)) instanceof PipeBlockEntity other
        && other.getPipePluggable(dir.getOpposite()) instanceof LensPluggable facing && facing.isFilter()) {
        DyeColor otherColor = facing.getColor();
        if (hasFilter) {
          if (otherColor != sideColor) continue;
        } else if (hasLens) {
          if (otherColor != sideColor) continue;
          // The lens gives the item the filter's colour on the way out.
        } else {
          hasFilter = true;
          sideColor = otherColor;
        }
      }

      if (!hasFilter) {
        unfiltered.add(dir);
      } else if (itemColor == sideColor) {
        matching.add(dir);
      }
    }

    destinations.clear();
    destinations.addAll(matching.isEmpty() ? unfiltered : matching);
  }

  /** Whether the item may leave through {@code dir}, for pipes that pick their own output (Iron, Diamond). */
  public static boolean canPass(PipeBlockEntity pipe, TravelingItem item, Direction dir) {
    List<Direction> single = new ArrayList<>(1);
    single.add(dir);
    filterDestinations(pipe, item, single);
    return !single.isEmpty();
  }
}
