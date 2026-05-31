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
package com.peco2282.bcreborn.transport;


import net.minecraft.nbt.CompoundTag;

public interface IEmeraldPipeBehaviorBehavior extends IFilteredPipeBehavior {

	enum FilterMode {
		WHITE_LIST, BLACK_LIST, ROUND_ROBIN
	}

	class EmeraldPipeSettings {
		private FilterMode filterMode;

		public EmeraldPipeSettings(FilterMode defaultMode) {
			filterMode = defaultMode;
		}

		public FilterMode getFilterMode() {
			return filterMode;
		}

		public void setFilterMode(FilterMode mode) {
			filterMode = mode;
		}

		public void readFromNBT(CompoundTag nbt) {
			filterMode = FilterMode.values()[nbt.getByte("filterMode")];
		}

		public void writeToNBT(CompoundTag nbt) {
			nbt.putByte("filterMode", (byte) filterMode.ordinal());
		}
	}

	EmeraldPipeSettings getSettings();

	boolean isValidFilterMode(FilterMode mode);
}
