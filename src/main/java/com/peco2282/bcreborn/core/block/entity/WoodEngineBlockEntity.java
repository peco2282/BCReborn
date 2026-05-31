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
  public void burning() {
    if (this.energyStorage != null && isRedstonePowered) {
      if (level.getGameTime() % 16 == 0) {
        this.energyStorage.generateEnergy(10, false);
      }
      if (energyStorage.getEnergyStored() > 0 && canPushEnergy()) {
        setPumping(true);
      } else {
        setPumping(false);
      }
    } else {
      setPumping(false);
    }
  }

  private boolean hasSent = false;

  @Override
  protected void pushEnergyToNeighbor() {
    if (level == null || level.isClientSide) return;
    if (energyStorage == null) return;

    if (progressPart == 2) {
      if (!hasSent) {
        hasSent = true;
        BlockPos outPos = getBlockPos().relative(orientation);
        net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(outPos);
        if (be != null) {
          boolean canConnect = false;
          if (be instanceof com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver receiver) {
            canConnect = receiver.canConnectRedstoneEngine(orientation.getOpposite());
          }

          if (canConnect) {
            be.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY, orientation.getOpposite()).ifPresent(target -> {
              int available = energyStorage.getEnergyStored();
              if (available > 0) {
                int accepted = target.receiveEnergy(available, false);
                if (accepted > 0) {
                  energyStorage.extractEnergy(accepted, false);
                }
              }
            });
          } else {
            // 接続先がない場合はエネルギーを捨てる
            energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
          }
        }
      }
    } else {
      hasSent = false;
    }
  }

  @Override
  protected EnergyStage computeStageFromHeat(float h) {
    double energyLevel = getEnergyLevel();
    if (energyLevel < 0.33f) { return EnergyStage.BLUE; }
    else if (energyLevel < 0.66f) { return EnergyStage.GREEN; }
    else if (energyLevel < 0.75f) { return EnergyStage.YELLOW; }
    else { return EnergyStage.RED; }
  }

  @Override
  protected float getPistonSpeed() {
    if (level == null) return 0.0f;
    if (level.isClientSide) {
      return switch (getEnergyStage()) {
        case BLUE -> 0.02F;
        case GREEN -> 0.04F;
        case YELLOW -> 0.08F;
        case RED -> 0.16F;
        default -> 0.01F;
      };
    }
    return Math.max(0.16f * getHeatLevel(), 0.01f);
  }
}
