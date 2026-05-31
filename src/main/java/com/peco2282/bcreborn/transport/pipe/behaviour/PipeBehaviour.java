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
package com.peco2282.bcreborn.transport.pipe.behaviour;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * パイプのマテリアルごとの固有ロジックを定義するインターフェース
 */
public interface PipeBehaviour extends PipeBlockEvent {
  /**
   * パイプのtick更新処理
   */
  default void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
  }

  /**
   * パイプが配置された時の処理
   */
  @Deprecated(forRemoval = true)
  default void onPlaced(PipeBlockEntity pipe) {
  }

  /**
   * パイプが破壊された時の処理
   */
  default void onRemoved(PipeBlockEntity pipe) {
  }

  /**
   * 接続するブロックと接続可能かどうか
   */
  default boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return true;
  }

  /**
   * 特定の方向から抽出が可能かどうか
   */
  default boolean canExtract(PipeBlockEntity pipe, Direction side) {
    return false;
  }

  /**
   * プレイヤーがパイプを右クリックした時の処理。
   * デフォルトは何もしない（PASS）。素材固有の動作はサブクラスでオーバーライドする。
   */
  default InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
    return InteractionResult.PASS;
  }
}
