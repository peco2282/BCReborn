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
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Iron Pipe: 強制出力方向を持つパイプ。
 * レンチで右クリックすることで出力方向を変更できる。
 * <p>
 * originalの PipeItemsIron / PipeLogicIron と同様に、
 * ironPipeOutput 方向へのみアイテムを出力する。
 */
public class IronItemPipeBehaviour implements ItemPipeBehaviour {

  public static final IronItemPipeBehaviour INSTANCE = new IronItemPipeBehaviour();

  private IronItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock) {
      // 他のパイプに対しては常に接続（入力側の接続）
      return true;
    }
    // マシン等に対しては出力方向のみ接続
    return pipe.getIronPipeOutput() == dir;
  }

  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);
    // レンチを持っている場合のみ方向変更
    if (!(heldItem.getItem() instanceof IToolWrench wrench)) {
      return InteractionResult.PASS;
    }
    if (!wrench.canWrench(player, pos.getX(), pos.getY(), pos.getZ())) {
      return InteractionResult.PASS;
    }

    if (!level.isClientSide) {
      // originalのPipeLogicIron.blockActivated(side)と同様に、クリックした方向に直接設定
      Direction clickedSide = hit.getDirection();
      Direction next = setFacing(pipe, level, pos, clickedSide);
      if (next == null) {
        // クリックした方向が無効な場合は次の有効方向を探す
        next = switchPosition(pipe, level, pos);
      }
      if (next != null) {
        player.displayClientMessage(Component.literal("Output direction: " + next.name()), true);
      }
      wrench.wrenchUsed(player, pos.getX(), pos.getY(), pos.getZ());
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /**
   * originalのPipeLogicIron.setFacing()に相当。
   * 指定方向が有効な出力方向であれば設定してその方向を返す。無効なら null を返す。
   */
  private Direction setFacing(PipeBlockEntity pipe, Level level, BlockPos pos, Direction facing) {
    if (facing == pipe.getIronPipeOutput()) return null;
    if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(facing))) return null;
    pipe.setIronPipeOutput(facing);
    level.sendBlockUpdated(pos, pipe.getBlockState(), pipe.getBlockState(), 3);
    return facing;
  }

  /**
   * originalのPipeLogicIron.switchPosition()に相当。
   * 現在の出力方向の次の有効な接続方向を探して設定する。
   */
  private Direction switchPosition(PipeBlockEntity pipe, Level level, BlockPos pos) {
    Direction current = pipe.getIronPipeOutput();
    Direction[] dirs = Direction.values();
    for (int i = 1; i <= dirs.length; i++) {
      Direction candidate = dirs[(current.ordinal() + i) % dirs.length];
      Direction result = setFacing(pipe, level, pos, candidate);
      if (result != null) return result;
    }
    return null;
  }

  @Override
  public Direction chooseNextDirection(PipeBlockEntity pipe, TravelingItem item) {
    Direction output = pipe.getIronPipeOutput();
    // 出力方向から来た場合は180度反転して来た方向に追い返す
    if (item.getEntryDirection() == output) {
      return output.getOpposite();
    }
    // 出力方向が接続されていて、来た方向でなければ強制出力
    if (pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(output))) {
      return output;
    }
    // 出力方向が塞がれている場合はデフォルトルーティングに委ねる
    return null;
  }
}
