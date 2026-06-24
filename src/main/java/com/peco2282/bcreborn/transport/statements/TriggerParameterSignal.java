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

import com.peco2282.bcreborn.BCReborn;
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
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public class TriggerParameterSignal implements IStatementParameter {

  @OnlyIn(Dist.CLIENT)
  private static TextureAtlasSprite[] icons;

  public boolean active = false;
  public PipeWire color = null;

  public TriggerParameterSignal() {

  }

  @Override
  public ItemStack getItemStack() {
    return ItemStack.EMPTY;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public TextureAtlasSprite getIcon() {
    if (color == null || icons == null) {
      return null;
    }

    return icons[color.ordinal() + (active ? 4 : 0)];
  }

  @Override
  public void onClick(@Nullable IStatementContainer source, @Nullable IStatement stmt, ItemStack stack, StatementMouseClick mouse) {
    int maxColor = 4;
    if (source instanceof Gate gate) {
      maxColor = gate.material.maxWireColor;
    }

    if (mouse.button() == 0) {
      if (color == null) {
        active = true;
        color = PipeWire.RED;
      } else if (active) {
        active = false;
      } else if (color == PipeWire.VALUES[maxColor - 1]) {
        color = null;
      } else {
        do {
          color = PipeWire.VALUES[(color.ordinal() + 1) & 3];
        } while (color.ordinal() >= maxColor);
        active = true;
      }
    } else {
      if (color == null) {
        active = false;
        color = PipeWire.VALUES[maxColor - 1];
      } else if (!active) {
        active = true;
      } else if (color == PipeWire.RED) {
        color = null;
      } else {
        do {
          color = PipeWire.VALUES[(color.ordinal() - 1) & 3];
        } while (color.ordinal() >= maxColor);
        active = false;
      }
    }
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
    nbt.putBoolean("active", active);

    if (color != null) {
      nbt.putByte("color", (byte) color.ordinal());
    }

  }

  @Override
  public void readFromNBT(CompoundTag nbt) {
    active = nbt.getBoolean("active");

    if (nbt.contains("color")) {
      color = PipeWire.VALUES[nbt.getByte("color")];
    }
  }

  @Override
  public String getDescription() {
    if (color == null) {
      return "";
    }
    return String.format(StringUtils.localize("gate.trigger.pipe.wire." + (active ? "active" : "inactive")), StringUtils.localize("color." + color.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public ResourceLocation getUniqueTag() {
    return BCReborn.getBasedLocation("pipe_wire_trigger");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icons = new TextureAtlasSprite[]{
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_red_inactive")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_blue_inactive")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_green_inactive")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_yellow_inactive")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_red_active")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_blue_active")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_green_active")),
      textureGetter.apply(BCRebornTransport.location("triggers/trigger_pipesignal_yellow_active"))
    };
  }

  @Override
  public IStatementParameter rotateLeft() {
    return this;
  }
}
