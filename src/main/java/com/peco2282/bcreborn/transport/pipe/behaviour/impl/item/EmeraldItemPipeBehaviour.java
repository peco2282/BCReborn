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

import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import com.peco2282.bcreborn.transport.menu.EmeraldPipeMenu;
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
import net.minecraft.world.level.LevelAccessor;
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
      // Wood-family pipes (Wood, Emerald, Emzuli; original PipeItemsWood subclasses) never connect to each other.
      return otherMat != PipeMaterial.WOOD && otherMat != PipeMaterial.EMERALD && otherMat != PipeMaterial.EMZULI;
    }
    // 抽出面として設定されている方向のインベントリのみ接続
    if (pipe.getExtractionSide() == dir) {
      var be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite()).isPresent();
    }
    return false;
  }

  /** Original PipeItemsEmerald extends PipeItemsWood: it picks its extraction side like the wooden pipe. */
  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    WoodenItemPipeBehaviour.INSTANCE.onPlace(pipe, level, oldState, isMoving);
  }

  @Override
  public void updateShape(PipeBlockEntity entity, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos neighborPos) {
    WoodenItemPipeBehaviour.INSTANCE.updateShape(entity, direction, neighbor, level, neighborPos);
  }

  /** Original PipeItemsEmerald extends PipeItemsWood: the wrench turns the extraction side. */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    return WoodenItemPipeBehaviour.INSTANCE.onWrenchUse(pipe, level, pos, player, hand, hit);
  }

  /** Original {@code PipeItemsEmerald.blockActivated()}: opens the filter GUI unless the player holds a pipe. */
  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
    ItemStack held = player.getItemInHand(hand);
    if (held.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof PipeBlock) {
      return InteractionResult.PASS;
    }
    if (player instanceof ServerPlayer serverPlayer) {
      NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
        (id, inv, user) -> new EmeraldPipeMenu(id, inv, pipe),
        Component.translatable("menu.bcreborntransport.emerald_pipe_menu")
      ), buf -> buf.writeBlockPos(pos));
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    FilteredItemExtractionModule.INSTANCE.extract(pipe);
  }
}
