/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.core.INBTStoreable;
import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An IPipePluggable MUST have an empty constructor for client-side rendering!
 */
public abstract class PipePluggable implements INBTStoreable, ISerializable {
    public abstract ItemStack[] getDropItems(IPipeTile pipe);

    public void update(IPipeTile pipe, Direction direction) {
    }

    public void onAttachedPipe(IPipeTile pipe, Direction direction) {
        validate(pipe, direction);
    }

    public void onDetachedPipe(IPipeTile pipe, Direction direction) {
        invalidate();
    }

    public abstract boolean isBlocking(IPipeTile pipe, Direction direction);

    public void invalidate() {
    }

    public void validate(IPipeTile pipe, Direction direction) {
    }

    public boolean isSolidOnSide(IPipeTile pipe, Direction direction) {
        return false;
    }

    public abstract AABB getBoundingBox(Direction side);

    @OnlyIn(Dist.CLIENT)
    public abstract IPipePluggableRenderer getRenderer();

    @OnlyIn(Dist.CLIENT)
    public IPipePluggableDynamicRenderer getDynamicRenderer() {
        return null;
    }

    public boolean requiresRenderUpdate(PipePluggable old) {
        return true;
    }
}
