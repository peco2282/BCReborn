/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.boards;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

public abstract class RedstoneBoardRegistry {
    public static RedstoneBoardRegistry instance;

    public abstract void registerBoardType(RedstoneBoardNBT<?> redstoneBoardNBT, int energyCost);

    @Deprecated
    public abstract void registerBoardClass(RedstoneBoardNBT<?> redstoneBoardNBT, float probability);

    public abstract void setEmptyRobotBoard(RedstoneBoardRobotNBT redstoneBoardNBT);

    public abstract RedstoneBoardRobotNBT getEmptyRobotBoard();

    public abstract RedstoneBoardNBT<?> getRedstoneBoard(CompoundTag nbt);

    public abstract RedstoneBoardNBT<?> getRedstoneBoard(String id);

    public abstract void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

    public abstract Collection<RedstoneBoardNBT<?>> getAllBoardNBTs();

    public abstract int getEnergyCost(RedstoneBoardNBT<?> board);
}
