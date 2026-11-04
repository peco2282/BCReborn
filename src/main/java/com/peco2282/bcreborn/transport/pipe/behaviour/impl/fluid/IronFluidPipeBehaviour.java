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

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import java.util.Collection;
import java.util.List;
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * 鉄液体パイプの挙動。
 * 出力方向が固定されており、レンチで方向を変更できる。
 * 転送速度は水晶と同じ（40mB/tick）。
 */
public class IronFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final IronFluidPipeBehaviour INSTANCE = new IronFluidPipeBehaviour();

  private IronFluidPipeBehaviour() {
  }


  @Override
  public boolean canOutputFluid(PipeBlockEntity pipe, Direction dir) {
    return dir == pipe.getIronPipeOutput();
  }

  /**
   * The original {@code PipeFluidsIron} also shares {@code PipeLogicIron} (switched by wrench and redstone).
   */
  @Override
  public InteractionResult onWrenchUse(PipeBlockEntity pipe, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
    return IronPipeLogic.onWrenchUse(pipe, level, pos, player, hand, hit);
  }

  @Override
  public void onPlace(PipeBlockEntity pipe, Level level, BlockState oldState, boolean isMoving) {
    if (!oldState.is(pipe.getBlockState().getBlock())) {
      IronPipeLogic.onPlaced(pipe, level);
    }
  }

  @Override
  public void addActions(PipeBlockEntity pipe, List<IActionInternal> actions) {
    IronPipeLogic.addDirectionActions(pipe, actions);
  }

  @Override
  public void onActionsActivated(PipeBlockEntity pipe, Collection<StatementSlot> actions) {
    IronPipeLogic.onActionsActivated(pipe, actions);
  }

  @Override
  public void onNeighborChanged(PipeBlockEntity pipe, Level level, BlockPos fromPos) {
    IronPipeLogic.switchOnRedstone(pipe, level);
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    IronPipeLogic.ensureInitialized(pipe, level);
    IronPipeLogic.syncAppearance(pipe, level);
  }

  @Override
  public void transferFluid(PipeBlockEntity pipe) {
    var fluidTank = pipe.getFluidTank();
    if (fluidTank == null || fluidTank.getFluidAmount() <= 0) return;

    int maxTransfer = pipe.getPipeMaterial().getFluidTransferRate();
    Direction output = pipe.getIronPipeOutput();

    BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(output));
    if (be == null) return;

    be.getCapability(ForgeCapabilities.FLUID_HANDLER, output.getOpposite()).ifPresent(handler -> {
      int amount = Math.min(fluidTank.getFluidAmount(), maxTransfer);
      FluidStack drained = fluidTank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
      int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
      fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
    });
  }
}
