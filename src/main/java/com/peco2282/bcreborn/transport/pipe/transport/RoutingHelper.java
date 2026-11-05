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
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.debug.PipeDebugLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
   * With no destination, pipes that allow bouncing return the entry side; every other pipe returns null and
   * the item is dropped at the centre (original PipeTransportItems.resolveDestination / allowBouncing).
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
      if (!isOpenTowards(pipe, dir)) continue;
      if (!canReceive(pipe, dir, item.getStack())) continue;
      possibleDirections.add(dir);
    }

    PipeBehaviour behaviour = pipe.getBehaviour();
    if (behaviour instanceof ItemPipeBehaviour ib) {
      Level level = pipe.getLevel();
      BlockPos pos = pipe.getBlockPos();
      List<Direction> restricted = new ArrayList<>();
      for (Direction dir : possibleDirections) {
        if (ib.canConnectTo(pipe, dir, level.getBlockState(pos.relative(dir)))) {
          restricted.add(dir);
        }
      }
      if (!restricted.isEmpty()) {
        possibleDirections = restricted;
      }
      ib.filterDestinations(pipe, item, possibleDirections);
    }

    if (possibleDirections.isEmpty()) {
      // Original allowBouncing (Iron, Daizuli, Clay): send the item back if the side it came from takes it.
      // Every other pipe has no destination, and the item is dropped at the centre.
      if (behaviour instanceof ItemPipeBehaviour ib && ib.allowsBouncing()
        && PipeBlock.isConnected(pipe.getBlockState(), from) && isOpenTowards(pipe, from) && canReceive(pipe, from, item.getStack())) {
        return from;
      }
      return null;
    }

    Level level = pipe.getLevel();
    RandomSource random = level.random;
    Direction chosen = possibleDirections.get(random.nextInt(possibleDirections.size()));
    PipeDebugLogger.logRouting(pipe, item, chosen);
    return chosen;
  }

  /**
   * Original {@code PipeTransportItems.getPossibleMovements()}: this pipe's output and the next pipe's input must
   * be open (gate valve action).
   */
  /**
   * Original {@code canReceivePipeObjects()}: the neighbour must be an item pipe, or an inventory that accepts at
   * least part of the stack.
   */
  static boolean canReceive(PipeBlockEntity pipe, Direction dir, ItemStack stack) {
    Level level = pipe.getLevel();
    if (level == null) return true;
    BlockPos next = pipe.getBlockPos().relative(dir);
    if (!level.isLoaded(next)) return false;
    BlockEntity be = level.getBlockEntity(next);
    if (be instanceof PipeBlockEntity nextPipe) {
      return nextPipe.getPipeType() == PipeType.ITEM;
    }
    if (be == null) return false;
    IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).orElse(null);
    return handler != null && ItemHandlerHelper.insertItemStacked(handler, stack.copy(), true).getCount() < stack.getCount();
  }

  static boolean isOpenTowards(PipeBlockEntity pipe, Direction dir) {
    if (!pipe.isOutputOpen(dir)) return false;
    Level level = pipe.getLevel();
    if (level == null) return true;
    BlockPos next = pipe.getBlockPos().relative(dir);
    return !(level.isLoaded(next) && level.getBlockEntity(next) instanceof PipeBlockEntity nextPipe)
      || nextPipe.isInputOpen(dir.getOpposite());
  }
}
