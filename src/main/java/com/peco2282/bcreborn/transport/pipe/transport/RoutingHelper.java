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
package com.peco2282.bcreborn.transport.pipe.transport;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.debug.PipeDebugLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Default routing implementation used when
 * ItemPipeBehaviour does not override routing.
 * <p>
 * 将来的に RoutingStrategy へ移行するための退避クラス。
 */
class RoutingHelper {

  /**
   * TravelingItem の次進行方向を決定する。
   * VOID パイプの場合は null を返す。
   * 行き場がない場合は来た方向（折り返し）を返す。
   * <p>
   * ランダム選択には Level.random (RandomSource) を使用する。
   * java.util.Random は Forge/NeoForge 1.20.1 では非推奨扱いのため使用しない。
   */
  Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    // getEntryDirection() はアイテムが「このパイプに入ってきた方向」を保持する。
    // routing 除外: 来た方向（entryDirection）には戻らない。
    Direction from = item.getEntryDirection();
    PipeMaterial pipeMaterial = pipe.getPipeMaterial();

    if (pipeMaterial == PipeMaterial.VOID) {
      return null;
    }

    List<Direction> possibleDirections = new ArrayList<>();
    for (Direction dir : Direction.values()) {
      if (dir == from) continue;
      if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(dir))) continue;
      possibleDirections.add(dir);
    }

    PipeBehaviour behaviour = pipe.getBehaviour();
    if (behaviour instanceof ItemPipeBehaviour ib) {
      Level level = pipe.getLevel();
      BlockPos pos = pipe.getBlockPos();
      List<Direction> restricted = new ArrayList<>();
      for (Direction dir : possibleDirections) {
        if (level != null && ib.canConnectTo(pipe, dir, level.getBlockState(pos.relative(dir)))) {
          restricted.add(dir);
        }
      }
      if (!restricted.isEmpty()) {
        possibleDirections = restricted;
      }
    }

    if (possibleDirections.isEmpty()) {
      return from;
    }

    Level level = pipe.getLevel();
    RandomSource random = (level != null) ? level.random : RandomSource.create();
    Direction chosen = possibleDirections.get(random.nextInt(possibleDirections.size()));
    PipeDebugLogger.logRouting(pipe, item, chosen);
    return chosen;
  }
}
