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
import com.peco2282.bcreborn.api.statements.containers.ISidedStatementContainer;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TriggerLightSensor extends BCStatement implements ITriggerInternal {
  private final boolean bright;

  public TriggerLightSensor(boolean bright) {
    super(BCRebornTransport.location("light_" + (bright ? "bright" : "dark")));
    this.bright = bright;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.light." + (bright ? "bright" : "dark"));
  }

  @Override
  public boolean isTriggerActive(@Nullable IStatementContainer source, IStatementParameter[] parameters) {
    if (source == null) return false;
    BlockEntity tile = source.getTile();
    if (tile == null || tile.getLevel() == null) {
      return false;
    }
    Level level = tile.getLevel();
    BlockPos pos = tile.getBlockPos();
    if (source instanceof ISidedStatementContainer sided) {
      pos = pos.relative(sided.getSide());
    }

    int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);

    return (lightLevel < 8) ^ bright;
  }


  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_light_" + (bright ? "bright" : "dark")));
  }
}
