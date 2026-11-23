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
package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

import org.jetbrains.annotations.Nullable;
import com.peco2282.bcreborn.transport.pipe.extraction.FluidExtractionModule;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.menu.EmeraldFluidPipeMenu;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.WoodPipeLogic;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * エメラルド液体パイプの挙動。
 * フィルタースロットに設定された流体タイプのみを抽出する（フィルターなしの場合は全流体を抽出）。
 * エンジン不要。抽出量は PipeMaterial.EMERALD の fluidTransferRate (40mB/tick)。
 */
public class EmeraldFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final EmeraldFluidPipeBehaviour INSTANCE = new EmeraldFluidPipeBehaviour();

  private static final int EXTRACT_COOLDOWN = 20;

  private EmeraldFluidPipeBehaviour() {
  }

  /** Original {@code PipeLogicWood}: the side faces a fluid handler and only the wrench turns it. */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (!level.isClientSide) WoodPipeLogic.onWrench(pipe, player);
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    WoodPipeLogic.switchSourceIfNeeded(pipe);
  }

  @Override
  public void updateShape(PipeBlockEntity entity, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos neighborPos) {
    WoodPipeLogic.chooseSourceIfNeeded(entity);
  }

  /** Original {@code PipeFluidsEmerald.blockActivated()}: opens the filter GUI unless the player holds a pipe. */
  @Override
  public InteractionResult onUse(PipeBlockEntity pipe, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof PipeBlock) {
      return InteractionResult.PASS;
    }
    if (player instanceof ServerPlayer serverPlayer) {
      NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
        (id, inv, user) -> new EmeraldFluidPipeMenu(id, inv, pipe),
        Component.translatable("menu.bcreborntransport.emerald_fluid_pipe_menu")
      ), buf -> buf.writeBlockPos(pos));
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Original {@code PipeFluidsWood.outputOpen()} (inherited): never back into the block the pipe pulls from. */
  @Override
  public boolean canOutputFluid(PipeBlockEntity pipe, Direction dir) {
    return dir != pipe.getExtractionSide();
  }

  /**
   * Original {@code PipeFluidsEmerald.extractFluid()}: like the wooden pipe, limited to the fluid in the filter
   * slot (any fluid without a filter). The original had one filter slot; the first filled one is used.
   */
  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;
    FluidExtractionModule.INSTANCE.extract(pipe, filter(pipe));
  }

  @Nullable
  private static FluidStack filter(PipeBlockEntity pipe) {
    var filterInv = pipe.getEmeraldFilters();
    if (filterInv == null) return null;
    for (int i = 0; i < filterInv.getContainerSize(); i++) {
      FluidStack fluid = FluidExtractionModule.filterFluid(filterInv.getItem(i));
      if (fluid != null) return fluid;
    }
    return null;
  }
}
