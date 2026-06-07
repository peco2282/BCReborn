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
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.core.statements.StatementParameterRedstoneGateSideOnly;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class TriggerRedstoneFaderInput extends BCStatement implements ITriggerInternal {

  public final int level;

  public TriggerRedstoneFaderInput(int level) {
    super(String.format("buildcraft:redtone.input.%02d", level));

    this.level = level;
  }

  @Override
  public String getDescription() {
    return String.format(StringUtils.localize("gate.trigger.redstone.input.level"), level);
  }

  @Override
  public boolean isTriggerActive(IStatementContainer container, IStatementParameter[] parameters) {
    if (!(container instanceof IRedstoneStatementContainer redstoneContainer)) {
      return false;
    }

    int inputLevel;
    if (parameters.length > 0 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly sideOnly && sideOnly.isOn) {
      if (container instanceof Gate gate) {
        inputLevel = redstoneContainer.getRedstoneInput(gate.getSide());
      } else {
        inputLevel = redstoneContainer.getRedstoneInput(null);
      }
    } else {
      inputLevel = redstoneContainer.getRedstoneInput(null);
    }

    return inputLevel == level;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location(String.format("triggers/redstone_%02d", level)));
  }

  @Override
  public IStatementParameter createParameter(int index) {
    IStatementParameter param = null;

    if (index == 0) {
      param = new StatementParameterRedstoneGateSideOnly();
    }

    return param;
  }

  @Override
  public int maxParameters() {
    return 1;
  }
}
