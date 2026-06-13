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

import com.peco2282.bcreborn.api.power.IRedstoneEngine;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.core.CoreBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class WoodEngineBlockEntity extends EngineBlockEntity<WoodEngineBlockEntity> implements IRedstoneEngine {
  private final boolean hasSent = false;

  public WoodEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(CoreBlockEntityTypes.WOODEN_ENGINE.get(), p_155229_, p_155230_);
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
      // 1 MJ = 10 FE
      // オリジナル: 青 1/16, 緑 1/8, 黄 1/4, 赤 1/2 MJ/t
      // ここでは熱を徐々に上げて出力を増やす
      heat = Math.min(2000, heat + 0.5f);
      energyStage = computeStageFromHeat(heat);

      int gen = switch (energyStage) {
        case BLUE -> 1; // ~0.625
        case GREEN -> 2; // ~1.25
        case YELLOW -> 4; // ~2.5
        case RED -> 8; // ~5.0
        default -> 1;
      };
      this.energyStorage.generateEnergy(gen, false);
      setPumping(energyStorage.getEnergyStored() > 0 && canPushEnergy());
    } else {
      heat = Math.max(0, heat - 1.0f);
      energyStage = computeStageFromHeat(heat);
      setPumping(false);
    }
  }

  @Override
  protected void pushEnergyToNeighbor() {
    if (level == null || level.isClientSide) return;
    if (energyStorage == null) return;

    BlockPos outPos = getBlockPos().relative(orientation);
    BlockEntity be = level.getBlockEntity(outPos);
    if (be != null) {
      be.getCapability(ForgeCapabilities.ENERGY, orientation.getOpposite()).ifPresent(target -> {
        int available = energyStorage.getEnergyStored();
        if (available > 0) {
          int accepted = target.receiveEnergy(available, false);
          if (accepted > 0) {
            energyStorage.extractEnergy(accepted, false);
          }
        }
      });
    } else {
      // 接続先がない場合はエネルギーを捨てる (木エンジンの特性)
      energyStorage.extractEnergy(energyStorage.getEnergyStored(), false);
    }
  }

  @Override
  protected EnergyStage computeStageFromHeat(float h) {
    if (h < 500) {
      return EnergyStage.BLUE;
    } else if (h < 1000) {
      return EnergyStage.GREEN;
    } else if (h < 1500) {
      return EnergyStage.YELLOW;
    } else {
      return EnergyStage.RED;
    }
  }

  @Override
  protected float getPistonSpeed() {
    if (!isActive()) {
      return 0;
    }
    return switch (energyStage) {
      case BLUE -> 0.01f;
      case GREEN -> 0.02f;
      case YELLOW -> 0.04f;
      case RED -> 0.08f;
      default -> 0.01f;
    };
  }
}
