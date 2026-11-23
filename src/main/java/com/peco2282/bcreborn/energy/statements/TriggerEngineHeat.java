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
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import java.util.Locale;

/** Original {@code TriggerEngineHeat}: the engine is at the given heat stage (blue, green, yellow, red, overheat). */
public class TriggerEngineHeat extends BCStatement implements ITriggerExternal {
  public final EngineBlockEntity.EnergyStage stage;

  public TriggerEngineHeat(EngineBlockEntity.EnergyStage stage) {
    super(BCRebornEnergy.location("engine.stage." + stage.name().toLowerCase(Locale.ENGLISH)));
    this.stage = stage;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.engine." + stage.name().toLowerCase(Locale.ENGLISH));
  }

  @Override
  public boolean isTriggerActive(BlockEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters) {
    return tile instanceof EngineBlockEntity<?> engine && engine.getEnergyStage() == stage;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornEnergy.location("triggers/trigger_engineheat_" + stage.name().toLowerCase(Locale.ENGLISH)));
  }
}
