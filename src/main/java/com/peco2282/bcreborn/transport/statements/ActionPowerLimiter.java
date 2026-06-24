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
import com.peco2282.bcreborn.api.transport.PowerMode;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Function;

public class ActionPowerLimiter extends BCStatement implements IActionInternal {

  public final PowerMode limit;

  public ActionPowerLimiter(PowerMode limit) {
    super(BCRebornTransport.location("power.limiter." + limit.name().toLowerCase(Locale.ENGLISH)));

    this.limit = limit;
  }

  @Override
  public String getDescription() {
    return limit.maxPower + " RF/t Limit";
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_limiter_" + limit.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public void actionActivate(IStatementContainer source,
                             IStatementParameter[] parameters) {

  }
}
