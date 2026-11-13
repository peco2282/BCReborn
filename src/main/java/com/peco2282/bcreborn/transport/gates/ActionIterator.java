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
package com.peco2282.bcreborn.transport.gates;


import com.peco2282.bcreborn.api.gates.IGate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.core.Direction;

import java.util.Iterator;

public class ActionIterator implements Iterable<StatementSlot> {
  private final IPipe pipe;

  public ActionIterator(IPipe iPipe) {
    pipe = iPipe;
  }

  /**
   * Original: walks the active actions of the gates on all six sides. The original stepped through
   * ForgeDirection up to UNKNOWN; Direction has no such terminator, so the slots are collected up front.
   */
  @Override
  public Iterator<StatementSlot> iterator() {
    List<StatementSlot> slots = new ArrayList<>();
    for (Direction side : Direction.values()) {
      IGate gate = pipe.getGate(side);
      if (gate != null) {
        slots.addAll(gate.getActiveActions());
      }
    }
    return Collections.unmodifiableList(slots).iterator();
  }
}
