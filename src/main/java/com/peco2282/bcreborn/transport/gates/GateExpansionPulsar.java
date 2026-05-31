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
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class GateExpansionPulsar extends GateExpansionBuildcraft implements IGateExpansion {

	public static GateExpansionPulsar INSTANCE = new GateExpansionPulsar();

	private GateExpansionPulsar() {
		super("pulsar");
	}

	@Override
	public GateExpansionController makeController(BlockEntity pipeTile) {
		return new GateExpansionControllerPulsar(pipeTile);
	}

	private class GateExpansionControllerPulsar extends GateExpansionController {

		private static final int PULSE_PERIOD = 10;
		private boolean isActive;
		private boolean singlePulse;
		private boolean hasPulsed;
		private int tick;
		private int count;

		public GateExpansionControllerPulsar(BlockEntity pipeTile) {
			super(GateExpansionPulsar.this, pipeTile);

			// by default, initialize tick so that not all gates created at
			// one single moment would do the work at the same time. This
			// spreads a bit work load. Note, this is not a problem for
			// existing gates since tick is stored in NBT
			tick = (int) (Math.random() * PULSE_PERIOD);
		}

		@Override
		public void startResolution() {
			if (isActive()) {
				disablePulse();
			}
		}

		@Override
		public boolean resolveAction(IStatement action, int count) {
			// TODO: ActionEnergyPulsar, ActionSingleEnergyPulse
			return false;
		}

		@Override
		public void addActions(List<IActionInternal> list) {
			super.addActions(list);
		}

		@Override
		public void tick(IGate gate) {
			if (!isActive && hasPulsed) {
				hasPulsed = false;
			}

			if (tick++ % PULSE_PERIOD != 0) {
				// only do the treatement once every period
				return;
			}

			if (!isActive) {
				gate.setPulsing(false);
				return;
			}

			// TODO: Energy pulse
			gate.setPulsing(true);
		}

		private void enableSinglePulse(int count) {
			singlePulse = true;
			isActive = true;
			this.count = count;
		}

		private void enablePulse(int count) {
			isActive = true;
			singlePulse = false;
			this.count = count;
		}

		private void disablePulse() {
			if (!isActive) {
				hasPulsed = false;
			}
			isActive = false;
			this.count = 0;
		}

		@Override
		public boolean isActive() {
			return isActive;
		}

		@Override
		public void writeToNBT(CompoundTag nbt) {
			nbt.putBoolean("singlePulse", singlePulse);
			nbt.putBoolean("isActive", isActive);
			nbt.putBoolean("hasPulsed", hasPulsed);
			nbt.putByte("pulseCount", (byte) count);
			nbt.putInt("tick", tick);
		}

		@Override
		public void readFromNBT(CompoundTag nbt) {
			isActive = nbt.getBoolean("isActive");
			singlePulse = nbt.getBoolean("singlePulse");
			hasPulsed = nbt.getBoolean("hasPulsed");
			count = nbt.getByte("pulseCount");
			tick = nbt.getInt("tick");
		}
	}
}
