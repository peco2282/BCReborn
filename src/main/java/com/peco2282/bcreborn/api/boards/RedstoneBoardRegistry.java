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

import com.peco2282.bcreborn.robotics.ImplRedstoneBoardRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

public abstract class RedstoneBoardRegistry {
  public static RedstoneBoardRegistry instance = new ImplRedstoneBoardRegistry();

  public abstract void registerBoardType(RedstoneBoardNBT<?> redstoneBoardNBT, int energyCost);

  @Deprecated
  public abstract void registerBoardClass(RedstoneBoardNBT<?> redstoneBoardNBT, float probability);

  public abstract RedstoneBoardRobotNBT getEmptyRobotBoard();

  public abstract void setEmptyRobotBoard(RedstoneBoardRobotNBT redstoneBoardNBT);

  public abstract RedstoneBoardNBT<?> getRedstoneBoard(CompoundTag nbt);

  public abstract RedstoneBoardNBT<?> getRedstoneBoard(String id);

  public abstract void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

  public abstract Collection<RedstoneBoardNBT<?>> getAllBoardNBTs();

  public abstract int getEnergyCost(RedstoneBoardNBT<?> board);
}
