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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.behaviour.EnergyPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * 木のエネルギーパイプの振る舞い。
 * エネルギーラインの始端（エネルギー源）としてのみ機能する。
 * エンジンなどのエネルギー源からエネルギーを吸い出し、
 * EnergyTransportModule 経由でパイプネットワークへ注入する。
 * 最大 320RF/t まで対応。
 */
public class WoodenEnergyPipeBehaviour implements EnergyPipeBehaviour {
  public static final WoodenEnergyPipeBehaviour INSTANCE = new WoodenEnergyPipeBehaviour();

  private WoodenEnergyPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    // 木のエネルギーパイプは他のエネルギーパイプへは接続しない（始端専用）
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      return otherPipe.getTransportType() != PipeType.ENERGY;
    }
    return true;
  }

  /**
   * エネルギー源からエネルギーを吸い出し、EnergyTransportModule へ注入する。
   * PipeBlockEntity.tick() → EnergyPipeBehaviour.extractEnergy() の順で呼ばれる。
   */
  @Override
  public void extractEnergy(PipeBlockEntity pipe) {
    EnergyTransportModule module = pipe.getEnergyTransportModule();
    if (module == null) return;

    int maxTransfer = PipeMaterial.WOOD.getEnergyTransferRate(); // 320 RF/t

    for (Direction dir : Direction.values()) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be == null || be instanceof PipeBlockEntity) continue;

      IEnergyStorage source = be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
      if (source == null || !source.canExtract()) continue;

      // 吸い出し可能量を計算（maxPower との差分を上限とする）
      int canReceive = maxTransfer - (int) module.getInternalPower()[dir.getOpposite().get3DDataValue()];
      if (canReceive <= 0) continue;

      int toExtract = Math.min(maxTransfer, canReceive);
      int extracted = source.extractEnergy(toExtract, true); // simulate
      if (extracted <= 0) continue;

      // EnergyTransportModule へ注入（損失なし: Wood は resistance=0）
      int accepted = module.receiveEnergy(dir.getOpposite(), extracted);
      if (accepted > 0) {
        source.extractEnergy(accepted, false); // 実際に抽出
      }
    }
  }
}
