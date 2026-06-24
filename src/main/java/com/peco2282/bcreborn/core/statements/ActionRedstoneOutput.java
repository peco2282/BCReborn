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
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.containers.IRedstoneStatementContainer;
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionRedstoneOutput extends BCStatement implements IActionInternal {

  public ActionRedstoneOutput(ResourceLocation s) {
    // Used by fader output
    super(s);
  }

  public ActionRedstoneOutput() {
    super(BCRebornCore.location("redstone.output"));
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.action.redstone.signal");
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

  protected boolean isSideOnly(IStatementParameter[] parameters) {
    if (parameters != null && parameters.length >= 1 && parameters[0] instanceof StatementParameterRedstoneGateSideOnly) {
      return ((StatementParameterRedstoneGateSideOnly) parameters[0]).isOn;
    }

    return false;
  }

  @Override
  public void actionActivate(IStatementContainer source,
                             IStatementParameter[] parameters) {
    if (source instanceof IRedstoneStatementContainer) {
      Direction side = null;
      if (source instanceof ISidedStatementContainer && isSideOnly(parameters)) {
        side = ((ISidedStatementContainer) source).getSide();
      }
      ((IRedstoneStatementContainer) source).setRedstoneOutput(side, getSignalLevel());
    }
  }

  protected int getSignalLevel() {
    return 15;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/action_redstoneoutput"));
  }
}
