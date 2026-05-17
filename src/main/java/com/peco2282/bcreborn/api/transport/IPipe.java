/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
