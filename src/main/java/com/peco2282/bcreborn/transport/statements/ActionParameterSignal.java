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
package com.peco2282.bcreborn.transport.statements;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementMouseClick;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.transport.gates.Gate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.function.Function;

public class ActionParameterSignal implements IStatementParameter {

  @OnlyIn(Dist.CLIENT)
  private static TextureAtlasSprite[] icons;

  public PipeWire color = null;

  public ActionParameterSignal() {

  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getIcon() {
    if (color == null) {
      return null;
    } else {
      return icons[color.ordinal()];
    }
  }

  @Override
  public void onClick(IStatementContainer source, IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    int maxColor = 4;
    if (source instanceof Gate gate) {
      maxColor = gate.material.maxWireColor;
    }

    if (color == null) {
      color = mouse.button() == 0 ? PipeWire.RED : PipeWire.VALUES[maxColor - 1];
    } else if (color == (mouse.button() == 0 ? PipeWire.VALUES[maxColor - 1] : PipeWire.RED)) {
      color = null;
    } else {
      do {
        color = PipeWire.VALUES[(mouse.button() == 0 ? color.ordinal() + 1 : color.ordinal() - 1) & 3];
      } while (color.ordinal() >= maxColor);
    }
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
    if (color != null) {
      nbt.putByte("color", (byte) color.ordinal());
    }
  }

  @Override
  public void readFromNBT(CompoundTag nbt) {
    if (nbt.contains("color")) {
      color = PipeWire.VALUES[nbt.getByte("color")];
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof ActionParameterSignal param) {

      return param.color == color;
    } else {
      return false;
    }
  }

  @Override
  public String getDescription() {
    if (color == null) {
      return "";
    }
    return String.format(StringUtils.localize("gate.action.pipe.wire"), StringUtils.localize("color." + color.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return BCRebornTransport.location("pipe_wire_action");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    // TODO: Register icons
  }

  @Override
  public IStatementParameter rotateLeft() {
    return this;
  }

  @Override
  public ItemStack getItemStack() {
    return null;
  }
}
