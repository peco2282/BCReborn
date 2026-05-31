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

import net.minecraft.core.Direction;

public interface IPipeTransportPowerHook {

	/**
	 * Override default behavior on receiving energy into the pipe.
	 *
	 * @return The amount of power used, or -1 for default behavior.
	 */
	int receiveEnergy(Direction from, int val);

	/**
	 * Override default requested power.
	 */
	int requestEnergy(Direction from, int amount);
}
