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

import com.peco2282.bcreborn.core.IWrench;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Emzuli Pipe: Lapis + Daizuliの機能を組み合わせたパイプ。
 * <p>
 * originalのPipeItemsEmzuliと同様に:
 * - アイテムにパイプの色タグを付与する（Lapisと同様）
 * - 色タグに基づいてフィルタースロットが一致する方向へルーティングする（Daizuliと同様）
 * - レンチで右クリックすると色を変更できる（Lapisと同様）
 * <p>
 * Lapis: 色タグを付与するだけでルーティングはしない
 * Daizuli: 既存の色タグでルーティングするが付与はしない
 * Emzuli: 色タグを付与しつつ、その色でルーティングも行う
 */
public class EmzuliItemPipeBehaviour implements ItemPipeBehaviour {

  public static final EmzuliItemPipeBehaviour INSTANCE = new EmzuliItemPipeBehaviour();

  private EmzuliItemPipeBehaviour() {
  }

  @Override
  public void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
    // Lapisと同様: 中央到達時にアイテムを色付けする
    ItemStack stack = item.getStack();
    if (!stack.isEmpty()) {
      var tag = stack.getOrCreateTag();
      tag.putInt("BCPipeColor", pipe.getPipeColor().getId());
    }
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    // Lapisと同様: 速度低下を1/4に抑制する
    float speed = item.getSpeed();
    float slowdown = (speed - 0.01f) * 0.01f;
    float adjustedSlowdown = slowdown / 4f;
    item.setSpeed(Math.max(0.01f, speed - adjustedSlowdown));
  }

  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);
    if (!(heldItem.getItem() instanceof IWrench wrench)) {
      return InteractionResult.PASS;
    }
    if (!wrench.canWrench(player, pos.getX(), pos.getY(), pos.getZ())) {
      return InteractionResult.PASS;
    }
    if (!level.isClientSide) {
      int current = pipe.getPipeColor().getId();
      int next;
      int len = DyeColor.values().length;
      if (player.isShiftKeyDown()) {
        next = (current + len - 1) % len;
      } else {
        next = (current + 1) % len;
      }
      pipe.setPipeColor(DyeColor.byId(next));
      player.displayClientMessage(Component.literal("Pipe color: " + next), true);
      wrench.wrenchUsed(player, pos.getX(), pos.getY(), pos.getZ());
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    // Daizuliと同様: BCPipeColorタグに一致するフィルタースロットを持つ方向へルーティング
    // onInjectItemで色タグを付与済みなので、自パイプの色と一致する方向を選ぶ
    ItemStack stack = item.getStack();
    if (stack.isEmpty() || !stack.hasTag()) {
      return null;
    }
    var tag = stack.getTag();
    if (tag == null || !tag.contains("BCPipeColor")) {
      return null;
    }
    int color = tag.getInt("BCPipeColor");
    for (Direction dir : Direction.values()) {
      if (dir == item.getEntryDirection()) continue;
      var filter = pipe.getFilter(dir);
      for (int i = 0; i < filter.getContainerSize(); i++) {
        ItemStack filterStack = filter.getItem(i);
        if (!filterStack.isEmpty() && filterStack.hasTag()) {
          var filterTag = filterStack.getTag();
          if (filterTag != null && filterTag.contains("BCPipeColor")
            && filterTag.getInt("BCPipeColor") == color) {
            return dir;
          }
        }
      }
    }
    return null;
  }
}
