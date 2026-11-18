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

import com.peco2282.bcreborn.transport.pipe.behaviour.impl.WoodPipeLogic;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.behaviour.ItemPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.extraction.StandardItemExtractionModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

/**
 * 木製アイテムパイプの振る舞い（抽出ロジック）
 * インベントリからアイテムを取り出す。抽出には動力（エンジン等）が必要。
 * 木・エメラルド以外のパイプとのみ接続し、抽出面のインベントリからアイテムを取り出す。
 */
public class WoodenItemPipeBehaviour implements ItemPipeBehaviour {

  public static final WoodenItemPipeBehaviour INSTANCE = new WoodenItemPipeBehaviour();

  private WoodenItemPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      PipeMaterial otherMat = otherPipe.getPipeMaterial();
      // Wood-family pipes (Wood, Emerald, Emzuli; original PipeItemsWood subclasses) never connect to each other.
      return otherMat != PipeMaterial.WOOD && otherMat != PipeMaterial.EMERALD && otherMat != PipeMaterial.EMZULI;
    }
    // 抽出面として設定されている方向のインベントリのみ接続
    if (pipe.getExtractionSide() == dir) {
      Level level = pipe.getLevel();
      var be = level.getBlockEntity(pipe.getBlockPos().relative(dir));
      return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).isPresent();
    }
    return false;
  }

  /** Original {@code PipeLogicWood.blockActivated()}: only the wrench turns the extraction side. */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    if (!level.isClientSide) WoodPipeLogic.onWrench(pipe, player);
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    StandardItemExtractionModule.INSTANCE.extract(pipe);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    WoodPipeLogic.switchSourceIfNeeded(pipe);
  }

  @Override
  public void updateShape(PipeBlockEntity entity, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos neighborPos) {
    WoodPipeLogic.chooseSourceIfNeeded(entity);
  }
}
