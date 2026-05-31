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
package com.peco2282.bcreborn.core.statements;

import net.minecraft.core.Direction;

/**
 * A tile entity implementing this interface will be able to prevent BuildCraft from
 * adding default triggers.
 * <p>
 * This does not block other statement providers from adding triggers or actions.
 * See IOverrideDefaultStatements for a more aggressive approach.
 */
public interface IBlockDefaultTriggers {
  boolean blockInventoryTriggers(Direction side);

  boolean blockFluidHandlerTriggers(Direction side);
}
