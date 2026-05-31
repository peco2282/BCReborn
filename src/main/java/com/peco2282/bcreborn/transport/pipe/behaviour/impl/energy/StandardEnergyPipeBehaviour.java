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
import com.peco2282.bcreborn.transport.pipe.behaviour.EnergyPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 標準エネルギーパイプの振る舞い。
 * エネルギー転送ロジックは EnergyTransportModule が一元管理するため、
 * このクラスは接続判定と最大転送量の提供のみを担当する。
 */
public abstract class StandardEnergyPipeBehaviour implements EnergyPipeBehaviour {

  /**
   * このパイプが1tickに転送できる最大RF量を返す。
   * サブクラスでオーバーライドして上限を変更できる。
   */
  protected int getMaxTransferRate(PipeMaterial material) {
    return material.getEnergyTransferRate();
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    PipeMaterial thisMat = pipe.getPipeMaterial();
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      PipeMaterial otherMat = otherPipe.getPipeMaterial();
      // 石パイプと丸石パイプは互いに接続しない
      return (thisMat != PipeMaterial.STONE || otherMat != PipeMaterial.COBBLESTONE) &&
        (thisMat != PipeMaterial.COBBLESTONE || otherMat != PipeMaterial.STONE);
    }
    return true;
  }
}
