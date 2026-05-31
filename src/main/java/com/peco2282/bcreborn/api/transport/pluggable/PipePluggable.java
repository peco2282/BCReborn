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
