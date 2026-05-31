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
package com.peco2282.bcreborn.core.statements;

import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class StatementParameterItemStackExact implements IStatementParameter {
  protected ItemStack stack = ItemStack.EMPTY;
  private int availableSlots;

  public StatementParameterItemStackExact() {
    this(-1);
  }

  public StatementParameterItemStackExact(int availableSlots) {
    this.availableSlots = availableSlots;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getIcon() {
    return null;
  }

  @Override
  public ItemStack getItemStack() {
    return stack;
  }

  @Override
  public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    if (!stack.isEmpty()) {
      if (ItemStack.isSameItemSameTags(this.stack, stack)) {
        if (mouse.button() == 0) {
          this.stack.grow((mouse.shift()) ? 16 : 1);

          int maxSize = availableSlots < 0 ? 64 : Math.min(64, this.stack.getMaxStackSize() * availableSlots);
          if (this.stack.getCount() > maxSize) {
            this.stack.setCount(maxSize);
          }
        } else {
          this.stack.shrink((mouse.shift()) ? 16 : 1);
          if (this.stack.getCount() <= 0) {
            this.stack = ItemStack.EMPTY;
          }
        }
      } else {
        this.stack = stack.copy();
      }
    } else {
      if (!this.stack.isEmpty()) {
        if (mouse.button() == 0) {
          this.stack.grow((mouse.shift()) ? 16 : 1);

          int maxSize = availableSlots < 0 ? 64 : Math.min(64, this.stack.getMaxStackSize() * availableSlots);
          if (this.stack.getCount() > maxSize) {
            this.stack.setCount(maxSize);
          }
        } else {
          this.stack.shrink((mouse.shift()) ? 16 : 1);
          if (this.stack.getCount() <= 0) {
            this.stack = ItemStack.EMPTY;
          }
        }
      }
    }
  }

  @Override
  public void writeToNBT(CompoundTag compound) {
    if (!stack.isEmpty()) {
      CompoundTag tagCompound = new CompoundTag();
      stack.save(tagCompound);
      compound.put("stack", tagCompound);
    }

    compound.putInt("availableSlots", availableSlots);
  }

  @Override
  public void readFromNBT(CompoundTag compound) {
    if (compound.contains("availableSlots")) {
      availableSlots = compound.getInt("availableSlots");
    }
    stack = ItemStack.of(compound.getCompound("stack"));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof StatementParameterItemStackExact param) {

      return ItemStack.isSameItemSameTags(stack, param.stack) && ItemStack.matches(stack, param.stack);
    } else {
      return false;
    }
  }

  @Override
  public String getDescription() {
    if (!stack.isEmpty()) {
      return stack.getDisplayName().getString();
    } else {
      return "";
    }
  }

  @Override
  public String getUniqueTag() {
    return "buildcraft:stackExact";
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {

  }

  @Override
  public IStatementParameter rotateLeft() {
    return this;
  }
}
