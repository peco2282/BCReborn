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
package com.peco2282.bcreborn.api.statements;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface IStatementParameter {
  Codec<IStatementParameter> CODEC = ResourceLocation.CODEC.dispatch(
    IStatementParameter::getUniqueTag,
    s -> StatementManager.getParameterCodec(s).get().orThrow()
  );

  ResourceLocation getUniqueTag();

  @Nullable
  @OnlyIn(Dist.CLIENT)
  TextureAtlasSprite getIcon();

  ItemStack getItemStack();

  @OnlyIn(Dist.CLIENT)
  void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

  @Nullable
  String getDescription();

  void onClick(@Nullable IStatementContainer source, @Nullable IStatement stmt, ItemStack stack, StatementMouseClick mouse);

  void readFromNBT(CompoundTag compound);

  void writeToNBT(CompoundTag compound);

  @Nullable
  IStatementParameter rotateLeft();
}
