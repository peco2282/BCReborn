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

import com.peco2282.bcreborn.transport.statements.TriggerClockTimer;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.api.gates.GateExpansionController;
import com.peco2282.bcreborn.api.gates.IGate;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class GateExpansionTimer extends GateExpansionBuildcraft implements IGateExpansion {

  public static GateExpansionTimer INSTANCE = new GateExpansionTimer();

  private GateExpansionTimer() {
    super("timer");
  }

  @Override
  public GateExpansionController makeController(BlockEntity pipeTile) {
    return new GateExpansionControllerTimer(pipeTile);
  }

  /** Original: one timer per {@link TriggerClockTimer.Time}; each is active for 5 ticks every 5 / 10 / 15 s. */
  private class GateExpansionControllerTimer extends GateExpansionController {

    private final Timer[] timers = new Timer[TriggerClockTimer.Time.VALUES.length];

    public GateExpansionControllerTimer(BlockEntity pipeTile) {
      super(GateExpansionTimer.this, pipeTile);
      for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
        timers[time.ordinal()] = new Timer(time);
      }
    }

    @Override
    public boolean isTriggerActive(IStatement trigger, IStatementParameter[] parameters) {
      if (trigger instanceof TriggerClockTimer timerTrigger) {
        return timers[timerTrigger.time.ordinal()].isActive();
      }
      return super.isTriggerActive(trigger, parameters);
    }

    @Override
    public void addTriggers(List<ITriggerInternal> list) {
      super.addTriggers(list);
      for (TriggerClockTimer.Time time : TriggerClockTimer.Time.VALUES) {
        list.add(TransportStatements.TRIGGER_CLOCK_TIMER.get(time).get());
      }
    }

    @Override
    public void tick(IGate gate) {
      for (Timer timer : timers) {
        timer.tick();
      }
    }

    private static class Timer {

      private static final int ACTIVE_TIME = 5;
      private final TriggerClockTimer.Time time;
      private int clock;

      public Timer(TriggerClockTimer.Time time) {
        this.time = time;
      }

      public void tick() {
        if (clock > -ACTIVE_TIME) {
          clock--;
        } else {
          clock = time.delay * 20 + ACTIVE_TIME;
        }
      }

      public boolean isActive() {
        return clock < 0;
      }
    }
  }
}
