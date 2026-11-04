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

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.ITriggerProvider;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Port of the original {@code buildcraft.transport.PipeTriggerProvider}: the gate's own triggers
 * (wire and expansion triggers) plus the pipe-contents triggers for the pipe type.
 */
public final class PipeTriggerProvider implements ITriggerProvider {
  public static final PipeTriggerProvider INSTANCE = new PipeTriggerProvider();

  private PipeTriggerProvider() {
  }

  @Override
  public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
    LinkedList<ITriggerInternal> result = new LinkedList<>();
    if (!(container.getTile() instanceof PipeBlockEntity pipe)) {
      return result;
    }
    if (container instanceof Gate gate) {
      gate.addTriggers(result);
    }
    switch (pipe.getPipeType()) {
      case ITEM -> add(result, PipeContents.empty, PipeContents.containsItems);
      case FLUID -> add(result, PipeContents.empty, PipeContents.containsFluids);
      case ENERGY -> add(result, PipeContents.empty, PipeContents.containsEnergy,
        PipeContents.tooMuchEnergy, PipeContents.requestsEnergy);
    }
    return result;
  }

  private static void add(List<ITriggerInternal> result, PipeContents... contents) {
    for (PipeContents c : contents) {
      result.add(TransportStatements.TRIGGER_PIPE_CONTENTS.get(c).get());
    }
  }

  @Override
  public Collection<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity tile) {
    return new LinkedList<>();
  }
}
