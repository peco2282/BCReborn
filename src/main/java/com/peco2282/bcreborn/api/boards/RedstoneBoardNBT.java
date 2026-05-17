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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public abstract class RedstoneBoardNBT<T> {

    private static Random rand = new Random();

    public abstract String getID();

    public abstract void addInformation(ItemStack stack, Player player, List<?> list, boolean advanced);

    public abstract IRedstoneBoard<T> create(CompoundTag nbt, T object);

    @OnlyIn(Dist.CLIENT)
    public abstract void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

    @OnlyIn(Dist.CLIENT)
    public abstract TextureAtlasSprite getIcon(CompoundTag nbt);

    public void createBoard(CompoundTag nbt) {
        nbt.putString("id", getID());
    }

    public int getParameterNumber(CompoundTag nbt) {
        if (!nbt.contains("parameters")) {
            return 0;
        } else {
            return nbt.getList("parameters", 10).size();
        }
    }

    public float nextFloat(int difficulty) {
        return 1F - (float) Math.pow(rand.nextFloat(), 1F / difficulty);
    }
}
