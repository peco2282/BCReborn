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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.BCReborn;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class StatementParameterItemStack implements IStatementParameter {
  public static final Codec<StatementParameterItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ItemStack.CODEC.fieldOf("stack").forGetter(p -> p.stack)
  ).apply(instance, StatementParameterItemStack::new));

  protected ItemStack stack;
  private ResourceLocation tag = BCReborn.getBasedLocation("stack");

  public StatementParameterItemStack() {
    this(ItemStack.EMPTY);
  }

  public StatementParameterItemStack(ResourceLocation tag) {
    this.tag = tag;
  }

  public StatementParameterItemStack(ItemStack stack) {
    this.stack = stack;
  }

  @Nullable
  @Override
  public TextureAtlasSprite getIcon() {
    return null;
  }

  @Override
  public ItemStack getItemStack() {
    return stack;
  }

  @Override
  public void onClick(@Nullable IStatementContainer source, @Nullable IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    if (!stack.isEmpty()) {
      this.stack = stack.copy();
      this.stack.setCount(1);
    } else {
      this.stack = ItemStack.EMPTY;
    }
  }

  @Override
  public void writeToNBT(CompoundTag compound) {
    if (!stack.isEmpty()) {
      CompoundTag tagCompound = new CompoundTag();
      stack.save(tagCompound);
      compound.put("stack", tagCompound);
    }
  }

  @Override
  public void readFromNBT(CompoundTag compound) {
    stack = ItemStack.of(compound.getCompound("stack"));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof StatementParameterItemStack param) {
      return ItemStack.matches(stack, param.stack)
        && ItemStack.isSameItemSameTags(stack, param.stack);
    } else {
      return false;
    }
  }

  @Nullable
  @Override
  public String getDescription() {
    if (stack != null) {
      return stack.getDisplayName().getString();
    } else {
      return "";
    }
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return tag;
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
  }

  @Nullable
  @Override
  public IStatementParameter rotateLeft() {
    return this;
  }
}
