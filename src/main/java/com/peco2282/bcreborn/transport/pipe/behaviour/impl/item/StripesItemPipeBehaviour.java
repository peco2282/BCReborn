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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.item;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ストライプアイテムパイプの振る舞い。
 * パイプ内のアイテムを使ってブロックを設置したり、前方のブロックを破壊したりする特殊パイプ。
 * アイテムパイプに接続可能。
 */
public class StripesItemPipeBehaviour implements ItemPipeBehaviour {

  public static final StripesItemPipeBehaviour INSTANCE = new StripesItemPipeBehaviour();

  private StripesItemPipeBehaviour() {
  }


  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    Level level = pipe.getLevel();
    if (level == null) return true;
    BlockPos neighborPos = pipe.getBlockPos().relative(dir);
    BlockEntity be = level.getBlockEntity(neighborPos);
    if (be instanceof PipeBlockEntity neighborPipe) {
      PipeMaterial mat = neighborPipe.getPipeMaterial();
      return mat != PipeMaterial.OBSIDIAN && mat != PipeMaterial.STRIPES;
    }
    return true;
  }

  @Override
  public void onInjectItem(PipeBlockEntity pipe, ItemStack stack, Direction from, float speed) {
    if (stack.isEmpty()) return;
    Level level = pipe.getLevel();
    if (level == null || level.isClientSide) return;

    // 突端チェック：接続されているパイプ方向が2方向以上の場合は動作しない
    BlockState state = level.getBlockState(pipe.getBlockPos());
    long connectedCount = PipeBlock.PROPERTY_MAP.values().stream()
      .filter(state::getValue)
      .count();
    if (connectedCount >= 2) return;

    // 来た方向の逆（出口方向）
    Direction outputDir = from.getOpposite();
    BlockPos targetPos = pipe.getBlockPos().relative(outputDir);
    BlockState targetState = level.getBlockState(targetPos);

    if (stack.getItem() instanceof BlockItem blockItem) {
      // 設置処理
      if (targetState.isAir() || targetState.canBeReplaced()) {
        BlockState placeState = blockItem.getBlock().defaultBlockState();
        if (placeState.canSurvive(level, targetPos)) {
          level.setBlock(targetPos, placeState, 3);
          stack.shrink(1); // 設置したブロック分を消費
        }
      }
    } else {
      // ブロック破壊処理（ストライプパイプがツール等のアイテムとして注入された場合）
      // BC 1.7.10 ではアイテムが注入された時、それがツールでなくても破壊を試みることがある
      // ここでは、非ブロックアイテムが「空気でないブロック」に対して注入された場合、それを破壊してドロップさせる
      if (!targetState.isAir() && targetState.getDestroySpeed(level, targetPos) >= 0) {
        level.destroyBlock(targetPos, true);
        stack.shrink(1);
      } else {
        // 耐久値のあるアイテム等：使用処理を行い、残ったアイテムをターゲット位置にドロップ
        if (stack.isDamageableItem()) {
          stack.setDamageValue(stack.getDamageValue() + 1);
          if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.setCount(0);
          }
        } else {
          stack.shrink(1);
        }
        // 使用後に残ったアイテムをターゲット位置にドロップ
        if (!stack.isEmpty()) {
          double x = targetPos.getX() + 0.5;
          double y = targetPos.getY() + 0.5;
          double z = targetPos.getZ() + 0.5;
          ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack.copy());
          itemEntity.setDefaultPickUpDelay();
          level.addFreshEntity(itemEntity);
          stack.setCount(0);
        }
      }
    }
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    float speed = item.getSpeed();
    float minSpeed = 0.01f;
    float maxSpeed = 0.15f;
    item.setSpeed(Math.max(minSpeed, Math.min(maxSpeed, speed - 0.001f)));
  }
}
