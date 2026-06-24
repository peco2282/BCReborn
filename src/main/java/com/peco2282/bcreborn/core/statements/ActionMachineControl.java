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
import com.peco2282.bcreborn.api.statements.IActionExternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.function.Function;


public class ActionMachineControl extends BCStatement implements IActionExternal {
  public final IControllable.Mode mode;

  public ActionMachineControl(IControllable.Mode mode) {
    super(BCRebornCore.location("machine." + mode.name().toLowerCase(Locale.ENGLISH)));

    this.mode = mode;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.action.machine." + mode.name().toLowerCase(Locale.ENGLISH));
  }

  @Override
  public void actionActivate(BlockEntity target, Direction side,
                             IStatementContainer source, IStatementParameter[] parameters) {
    if (target instanceof IControllable) {
      ((IControllable) target).setControlMode(mode);
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/action_machinecontrol_" + mode.name().toLowerCase(Locale.ENGLISH)));
  }
}
