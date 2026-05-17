/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
