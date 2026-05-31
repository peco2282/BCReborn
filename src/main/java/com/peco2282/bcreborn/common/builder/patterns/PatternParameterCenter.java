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
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class PatternParameterCenter implements IStatementParameter {
  public static final Codec<PatternParameterCenter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    Codec.INT.fieldOf("direction").forGetter(p -> p.direction)
  ).apply(instance, PatternParameterCenter::new));
  private static final TextureAtlasSprite[] ICONS = new TextureAtlasSprite[9];
  private static final int[] shiftLeft = {6, 3, 0, 7, 4, 1, 8, 5, 2};
  private int direction;

  public PatternParameterCenter() {
  }

  public PatternParameterCenter(int direction) {
    this.direction = direction;
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return BCReborn.getBasedLocation("filler_parameter_center");
  }

  @Override
  public TextureAtlasSprite getIcon() {
    return ICONS[direction];
  }

  @Override
  public ItemStack getItemStack() {
    return null;
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    for (int i = 0; i < 9; i++) {
      ICONS[i] = textureGetter.apply(BCRebornCore.location("item/filler_parameter/center_" + i + ".png"));
    }
  }

  @Override
  public String getDescription() {
    return "direction.center." + direction;
  }

  @Override
  public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    direction = (direction + 1) % 9;
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
    return new PatternParameterCenter(shiftLeft[direction % 9]);
  }

  public int getDirection() {
    return direction;
  }
}
