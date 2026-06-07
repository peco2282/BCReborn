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


import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.core.Direction;

import java.util.Iterator;

public class ActionIterator implements Iterable<StatementSlot> {
  private final IPipe pipe;

  public ActionIterator(IPipe iPipe) {
    pipe = iPipe;
  }

  @Override
  public Iterator<StatementSlot> iterator() {
    return new It();
  }

  private class It implements Iterator<StatementSlot> {

    private Direction curDir = Direction.values()[0];
    private int index = 0;
    private StatementSlot next;

    public It() {
      while (!isValid()) {
        if (curDir == Direction.UP) {
          break;
        } else if (pipe.getGate(curDir) == null
          || index >= pipe.getGate(curDir).getActiveActions().size() - 1) {
          index = 0;
          curDir = Direction.values()[curDir.ordinal() + 1];
        } else {
          index++;
        }
      }

      if (isValid()) {
        next = pipe.getGate(curDir).getActiveActions().get(index);
      }
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public StatementSlot next() {
      StatementSlot result = next;

      while (true) {
        if (index < Gate.MAX_STATEMENTS - 1) {
          index++;
        } else if (curDir != Direction.UP) {
          index = 0;
          curDir = Direction.values()[curDir.ordinal() + 1];
        } else {
          break;
        }

        if (isValid()) {
          break;
        }
      }

      if (isValid()) {
        next = pipe.getGate(curDir).getActiveActions().get(index);
      } else {
        next = null;
      }

      return result;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Remove not supported.");
    }

    private boolean isValid() {
      return curDir != null
        && pipe.getGate(curDir) != null
        && index < pipe.getGate(curDir).getActiveActions().size();
    }
  }
}
