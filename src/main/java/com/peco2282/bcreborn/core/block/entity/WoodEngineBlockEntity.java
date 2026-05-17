package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.api.IRedstoneEngine;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class WoodEngineBlockEntity extends EngineBlockEntity<WoodEngineBlockEntity> implements IRedstoneEngine {
  public WoodEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BlockEntityTypesCore.WOODEN_ENGINE.get(), p_155229_, p_155230_);
    // 小容量・小出力
    configureEnergy(5000, 40);
  }

  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.core().addPath("wood_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    return false;
  }

  @Override
  public boolean isBurning() {
    // レッドストーン信号で常時微量出力
    return level.hasNeighborSignal(worldPosition);
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
  public void burinig() {
    if (this.energyStorage != null) {
      // 微量発電（5FE/t）
      this.energyStorage.generateEnergy(5, false);
    }
  }
}
