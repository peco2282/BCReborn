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

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TriggerRedstoneInput extends BCStatement implements ITriggerInternal {

  boolean active;

  public TriggerRedstoneInput(boolean active) {
    super(BCRebornCore.location("redstone.input." + (active ? "active" : "inactive")));
    this.active = active;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.redstone.input." + (active ? "active" : "inactive"));
  }

  @Override
  @Nullable
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

  @Override
  public boolean isTriggerActive(@Nullable IStatementContainer container, IStatementParameter[] parameters) {
    if (container instanceof IRedstoneStatementContainer) {
      int level = ((IRedstoneStatementContainer) container).getRedstoneInput(null);
      if (parameters.length > 0 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly &&
        ((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn &&
        container instanceof ISidedStatementContainer) {
        level = ((IRedstoneStatementContainer) container).getRedstoneInput(((ISidedStatementContainer) container).getSide());
      }

      return active ? level > 0 : level == 0;
    } else {
      return false;
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_redstoneinput_" + (active ? "active" : "inactive")));
  }
}
