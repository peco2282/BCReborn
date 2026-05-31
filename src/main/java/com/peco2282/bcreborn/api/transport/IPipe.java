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
package com.peco2282.bcreborn.api.transport;

import com.peco2282.bcreborn.api.gates.IGate;
import net.minecraft.core.Direction;

public interface IPipe {
    IPipeTile getTile();

    IGate getGate(Direction side);

    boolean hasGate(Direction side);

    boolean isWired(PipeWire wire);

    boolean isWireActive(PipeWire wire);
}
