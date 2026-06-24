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
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;
import java.util.function.Function;

public class ActionPipeColor extends BCStatement implements IActionInternal {

  public final DyeColor color;

  public ActionPipeColor(DyeColor color) {
    super(BCRebornTransport.location("pipe.color." + color.getSerializedName()));

    this.color = color;
  }

  @Override
  public String getDescription() {
    return String.format(StringUtils.localize("gate.action.pipe.item.color"), color.getName());
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location("paintbrush/" + color.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public void actionActivate(IStatementContainer source,
                             IStatementParameter[] parameters) {

  }
}
