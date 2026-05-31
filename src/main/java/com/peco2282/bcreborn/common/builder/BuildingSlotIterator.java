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
package com.peco2282.bcreborn.common.builder;

import java.util.Iterator;
import java.util.LinkedList;

public class BuildingSlotIterator {
  private static final int ITERATIONS_MAX = 500;

  private final LinkedList<BuildingSlotBlock> buildList;
  private Iterator<BuildingSlotBlock> current;
  private int nbIterations;

  /**
   * Creates an iterator on the list, which will cycle through iterations per
   * chunk.
   */
  public BuildingSlotIterator(LinkedList<BuildingSlotBlock> buildList) {
    this.buildList = buildList;
  }

  public void startIteration() {
    if (current == null || !current.hasNext()) {
      current = buildList.iterator();
    }

    nbIterations = 0;
  }

  public boolean hasNext() {
    return current.hasNext() && nbIterations < ITERATIONS_MAX;
  }

  public BuildingSlotBlock next() {
    BuildingSlotBlock next = current.next();

    if (next == null) {
      // we're only accepting to pass through a null element if this is
      // the first iteration. Otherwise, elements before null need to
      // be worked out.
      if (nbIterations == 0) {
        current.remove();
      }
    }

    nbIterations++;
    return next;
  }

  public void remove() {
    current.remove();
  }

  public void reset() {
    current = buildList.iterator();
    nbIterations = 0;
  }

}
