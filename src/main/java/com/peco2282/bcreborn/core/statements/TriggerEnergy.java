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
import com.peco2282.bcreborn.common.utils.StringUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.function.Function;

public class TriggerEnergy extends BCStatement implements ITriggerInternal {

  private final boolean high;

  public TriggerEnergy(boolean high) {
    super("buildcraft:energyStored" + (high ? "high" : "low"));

    this.high = high;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.trigger.machine.energyStored." + (high ? "high" : "low"));
  }

  protected boolean isActive(BlockEntity tile, Direction side) {
    if (tile == null) return false;
    return tile.getCapability(ForgeCapabilities.ENERGY, side).map(storage -> {
      if (storage.getMaxEnergyStored() > 0) {
        float level = (float) storage.getEnergyStored() / (float) storage.getMaxEnergyStored();
        if (high) {
          return level > 0.95F;
        } else {
          return level < 0.05F;
        }
      }
      return false;
    }).orElse(false);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornCore.location("triggers/trigger_energy_storage_" + (high ? "high" : "low")));
  }

  @Override
  public boolean isTriggerActive(IStatementContainer source, IStatementParameter[] parameters) {
    BlockEntity tile = source.getTile();
    if (tile == null) return false;

    // Check the tile itself first
    if (isActive(tile, null)) return true;

    // Check neighbors
    for (Direction side : Direction.values()) {
      BlockEntity neighbor = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(side));
      if (isActive(neighbor, side.getOpposite())) return true;
    }

    return false;
  }
}
