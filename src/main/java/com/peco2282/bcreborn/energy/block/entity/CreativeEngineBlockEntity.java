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
package com.peco2282.bcreborn.energy.block.entity;

import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.energy.BlockEntityTypesEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeEngineBlockEntity extends EngineBlockEntity<CreativeEngineBlockEntity> {
  public CreativeEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BlockEntityTypesEnergy.CREATIVE_ENGINE.get(), p_155229_, p_155230_);
  }

  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.energy("creative_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBurning() {
    return true;
  }

  @Override
  public void updateProgress() {

  }

  @Override
  public void overheat() {

  }

  @Override
  public void explode() {

  }

  @Override
  public void burning() {
    if (this.energyStorage != null) {
      this.energyStorage.generateEnergy(this.energyStorage.getMaxEnergyStored(), false);
    }
    if (canPushEnergy()) {
      setPumping(true);
    } else {
      setPumping(false);
    }
  }
}
