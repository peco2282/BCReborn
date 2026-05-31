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
package com.peco2282.bcreborn.api.boards;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public abstract class RedstoneBoardNBT<T> {

  private static final Random rand = new Random();

  public abstract String getID();

  public abstract void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

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
