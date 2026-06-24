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
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.ActionRedstoneOutput;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionRedstoneFaderOutput extends ActionRedstoneOutput implements IActionInternal {

  public final int level;

  public ActionRedstoneFaderOutput(int level) {
    super(BCRebornTransport.location(String.format("redstone.output.%02d", level)));

    this.level = level;
  }

  @Override
  public String getDescription() {
    return String.format(StringUtils.localize("gate.trigger.redstone.input.level"), level);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location(String.format("triggers/redstone_%02d", level)));
  }

  @Override
  protected int getSignalLevel() {
    return level;
  }
}
