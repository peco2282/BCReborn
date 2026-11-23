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
package com.peco2282.bcreborn.energy.statements;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;

/** Original {@code TriggerFuelBelowThreshold}: the combustion engine's fuel tank is below the threshold. */
public class TriggerFuelBelowThreshold extends BCStatement implements ITriggerExternal {
  private final float threshold;

  public TriggerFuelBelowThreshold(float threshold) {
    super(BCRebornEnergy.location("trigger.fuellevelbelow." + (int) (threshold * 100)));
    this.threshold = threshold;
  }

  @Override
  public String getDescription() {
    return String.format(StringUtils.localize("gate.trigger.fuelLevelBelow"), (int) (threshold * 100));
  }

  @Override
  public boolean isTriggerActive(BlockEntity target, Direction side, IStatementContainer source, IStatementParameter[] parameters) {
    return target instanceof IronEngineBlockEntity engine && engine.hasFuelBelowThreshold(threshold);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornEnergy.location("triggers/trigger_fuel_below_threshold"));
  }
}
