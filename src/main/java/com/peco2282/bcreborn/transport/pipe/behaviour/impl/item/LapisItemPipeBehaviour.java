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

import com.peco2282.bcreborn.api.IToolWrench;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.statements.ActionPipeColor;
import java.util.Collection;
import java.util.List;
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
 * Lapis Pipe: 通過するアイテムにルーティング用の色を付与する。
 * ダイヤモンドパイプやゲートと組み合わせて色分けに使う。
 * <p>
 * originalの PipeItemsLapis と同様に:
 * - アイテムに色タグを付与する（ReachedCenter相当）
 * - 速度低下を1/4に抑制する（AdjustSpeed: slowdownAmount /= 4 相当）
 * - レンチで右クリックすると色を変更できる
 */
public class LapisItemPipeBehaviour implements ItemPipeBehaviour {

  public static final LapisItemPipeBehaviour INSTANCE = new LapisItemPipeBehaviour();

  private LapisItemPipeBehaviour() {
  }

  @Override
  public void onReachedCenter(PipeBlockEntity pipe, TravelingItem item) {
    // originalのReachedCenter eventHandlerと同様に、中央到達時にアイテムを色付けする
    ItemStack stack = item.getStack();
    if (!stack.isEmpty()) {
      var tag = stack.getOrCreateTag();
      tag.putInt("BCPipeColor", pipe.getPipeColor().getId());
    }
  }

  /** Original {@code PipeItemsLapis.getActions()}: one "pipe colour" action per dye colour. */
  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    for (DyeColor color : DyeColor.values()) {
      actions.add(TransportStatements.ACTION_PIPE_COLOR.get(color).get());
    }
  }

  /** Original {@code PipeItemsLapis.actionsActivated()}: the first active colour action recolours the pipe. */
  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    for (StatementSlot slot : actions) {
      if (slot.statement instanceof ActionPipeColor color) {
        if (pipe.getPipeColor() != color.color) {
          pipe.setPipeColor(color.color);
        }
        break;
      }
    }
  }

  @Override
  public void adjustSpeed(PipeBlockEntity pipe, TravelingItem item) {
    // originalのAdjustSpeed eventHandler: slowdownAmount /= 4 に相当
    float speed = item.getSpeed();
    float slowdown = (speed - 0.01f) * 0.01f; // 通常の速度低下量
    float adjustedSlowdown = slowdown / 4f;    // 1/4に抑制
    item.setSpeed(Math.max(0.01f, speed - adjustedSlowdown));
  }

  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);
    if (!(heldItem.getItem() instanceof IToolWrench wrench)) {
      return InteractionResult.PASS;
    }
    if (!wrench.canWrench(player, pos)) {
      return InteractionResult.PASS;
    }

    if (!level.isClientSide) {
      // originalと同様: スニーク時は前の色、通常時は次の色
      int current = pipe.getPipeColor().getId();
      int next;
      int len = DyeColor.values().length;
      if (player.isShiftKeyDown()) {
        next = (current + len - 1) % len; // 前の色（0〜15循環）
      } else {
        next = (current + 1) % len;  // 次の色
      }
      pipe.setPipeColor(DyeColor.byId(next));
      player.displayClientMessage(Component.literal("Pipe color: " + next), true);
      wrench.wrenchUsed(player, pos);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    // アイテムのBCPipeColorタグに一致するフィルタースロットを持つ方向へルーティング
    // originalのPipeItemsLapis: 色タグに基づいてダイヤモンドパイプ等と連携するルーティング
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
