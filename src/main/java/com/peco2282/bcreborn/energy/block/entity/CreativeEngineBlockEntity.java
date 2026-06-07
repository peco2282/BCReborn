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
import com.peco2282.bcreborn.energy.EnergyBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeEngineBlockEntity extends EngineBlockEntity<CreativeEngineBlockEntity> {
  public CreativeEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(EnergyBlockEntityTypes.CREATIVE_ENGINE.get(), p_155229_, p_155230_);
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
      // 常に最大容量まで生成
      this.energyStorage.generateEnergy(this.energyStorage.getMaxEnergyStored(), false);
    }
    // 常にピストンを動かす
    setPumping(true);
    // 常に赤段階の熱（最高速のため）
    heat = 800;
  }

  @Override
  protected void onPistonCycled() {
    pushEnergyToNeighbor();
  }
}
