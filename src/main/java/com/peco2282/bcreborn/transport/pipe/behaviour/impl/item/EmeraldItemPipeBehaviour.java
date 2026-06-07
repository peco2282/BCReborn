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
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.extraction.FilteredItemExtractionModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

/**
 * エメラルドアイテムパイプの振る舞い（フィルター付き抽出）
 * 木パイプの上位版。インベントリから指定したアイテムだけを抽出できる。
 * 木・エメラルド以外のパイプとのみ接続し、抽出面のインベントリからフィルター付きで取り出す。
 */
public class EmeraldItemPipeBehaviour implements ItemPipeBehaviour {

  public static final EmeraldItemPipeBehaviour INSTANCE = new EmeraldItemPipeBehaviour();

  private EmeraldItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      PipeMaterial otherMat = otherPipe.getPipeMaterial();
      // 木・エメラルドパイプ同士は接続しない
      return otherMat != PipeMaterial.WOOD && otherMat != PipeMaterial.EMERALD;
    }
    // 抽出面として設定されている方向のインベントリのみ接続
    if (pipe.getExtractionSide() == dir) {
      var be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).isPresent();
    }
    return false;
  }

  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (!level.isClientSide) {
      PipeBlockEntity.ExtractFilterMode nextMode = pipe.getExtractFilterMode().next();
      pipe.setExtractFilterMode(nextMode);
      player.displayClientMessage(Component.literal("Filter mode: " + nextMode.name()), true);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.getItemInHand(hand).getItem() instanceof IToolWrench) {
      return onWrenchUse(pipe, level, pos, player, hand, hit);
    }
    if (!level.isClientSide) {
      Direction current = pipe.getExtractionSide();
      Direction next = Direction.values()[(current.ordinal() + 1) % Direction.values().length];
      pipe.setExtractionSide(next);
      BlockState state = level.getBlockState(pos);
      BlockState newState = state;
      for (Direction dir : Direction.values()) {
        newState = newState.setValue(PipeBlock.PROPERTY_MAP.get(dir),
          state.getBlock() instanceof PipeBlock pb && pb.canConnectTo(level, pos, dir));
      }
      newState = newState.setValue(PipeBlock.EXTRACTION_SIDE, next.get3DDataValue());
      level.setBlock(pos, newState, 3);

      player.displayClientMessage(Component.literal("Extraction direction: " + next.name()), true);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    FilteredItemExtractionModule.INSTANCE.extract(pipe);
  }
}
