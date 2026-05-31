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
package com.peco2282.bcreborn.api.blueprints;


import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.world.level.Level;

/**
 * This interface provide contextual information when building or initializing
 * blueprint slots.
 */
public interface IBuilderContext {

	Position rotatePositionLeft(Position pos);

	IBox surroundingBox();

	Level world();

	MappingRegistry getMappingRegistry();
}
