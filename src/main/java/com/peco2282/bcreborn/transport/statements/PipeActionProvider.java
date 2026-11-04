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
package com.peco2282.bcreborn.transport.statements;

import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IActionProvider;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import java.util.Collection;
import java.util.LinkedList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Port of the original {@code buildcraft.transport.PipeActionProvider}: the gate's own actions (wire and expansion
 * actions) plus the pipe-specific actions of the pipe material ({@code Pipe.getActions()}).
 * Every pipe offers the valve actions (original base {@code Pipe.getActions()}).
 */
public final class PipeActionProvider implements IActionProvider {
  public static final PipeActionProvider INSTANCE = new PipeActionProvider();

  private PipeActionProvider() {
  }

  @Override
  public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
    LinkedList<IActionInternal> result = new LinkedList<>();
    if (container instanceof Gate gate) {
      gate.addActions(result);
    }
    if (container.getTile() instanceof PipeBlockEntity pipe) {
      for (ActionValve.ValveState state : ActionValve.ValveState.VALUES) {
        result.add(TransportStatements.ACTION_VALVE.get(state).get());
      }
      if (pipe.getBehaviour() != null) {
        pipe.getBehaviour().addActions(pipe, result);
      }
    }
    return result;
  }

  @Override
  public Collection<IActionExternal> getExternalActions(Direction side, BlockEntity tile) {
    return new LinkedList<>();
  }
}
