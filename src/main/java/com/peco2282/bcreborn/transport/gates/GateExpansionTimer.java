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

	private class GateExpansionControllerTimer extends GateExpansionController {

		private static class Timer {

			private static final int ACTIVE_TIME = 5;
			private final int delay;
			private int clock;

			public Timer(int delay) {
				this.delay = delay;
			}

			public void tick() {
				if (clock > -ACTIVE_TIME) {
					clock--;
				} else {
					clock = delay * 20 + ACTIVE_TIME;
				}
			}

			public boolean isActive() {
				return clock < 0;
			}
		}

		private final Timer[] timers = new Timer[8]; // Placeholder

		public GateExpansionControllerTimer(BlockEntity pipeTile) {
			super(GateExpansionTimer.this, pipeTile);
			for (int i = 0; i < timers.length; i++) {
				timers[i] = new Timer(1); // Placeholder
			}
		}

		@Override
		public boolean isTriggerActive(IStatement trigger, IStatementParameter[] parameters) {
			// TODO: TriggerClockTimer
			return super.isTriggerActive(trigger, parameters);
		}

		@Override
		public void addTriggers(List<ITriggerInternal> list) {
			super.addTriggers(list);
			// TODO: TriggerClockTimer
		}

		@Override
		public void tick(IGate gate) {
			for (Timer timer : timers) {
				timer.tick();
			}
		}
	}
}
