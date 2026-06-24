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
package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PatternParameterXZDir implements IStatementParameter {
  public static final Codec<PatternParameterXZDir> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.INT.fieldOf("dir").forGetter(p -> p.direction)
  ).apply(instance, PatternParameterXZDir::new));

  private static final String[] names = {"west", "east", "north", "south"};
  private static final int[] shiftLeft = {3, 2, 0, 1};
  private static final int[] shiftRight = {2, 3, 1, 0};
  private int direction;

  public PatternParameterXZDir() {
  }

  public PatternParameterXZDir(int direction) {
    this.direction = direction;
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return BCReborn.getBasedLocation("filler_parameter_xz_dir");
  }

  @Override
  public TextureAtlasSprite getIcon() {
    return null;
  }

  @Override
  public ItemStack getItemStack() {
    return ItemStack.EMPTY;
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
  }

  @Override
  public String getDescription() {
    return "direction." + names[direction & 3];
  }

  @Override
  public void onClick(@Nullable IStatementContainer source, @Nullable IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    direction = shiftRight[direction & 3];
  }

  @Override
  public void readFromNBT(CompoundTag compound) {
    direction = compound.getByte("dir");
  }

  @Override
  public void writeToNBT(CompoundTag compound) {
    compound.putByte("dir", (byte) direction);
  }

  @Override
  public IStatementParameter rotateLeft() {
    return new PatternParameterXZDir(shiftLeft[direction & 3]);
  }

  public int getDirection() {
    return direction;
  }
}
