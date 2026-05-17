package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 粘土液体パイプの挙動。
 * 接続しているパイプ以外のブロック（タンク等）を優先して転送する。
 * 複数の優先ターゲットがある場合はラウンドロビンで均等分配する。
 * 転送速度は40mB/tick。
 */
public class ClayFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final ClayFluidPipeBehaviour INSTANCE = new ClayFluidPipeBehaviour();

  private ClayFluidPipeBehaviour() {
  }

  @Override
  public void transferFluid(PipeBlockEntity pipe) {
    var fluidTank = pipe.getFluidTank();
    if (fluidTank == null || fluidTank.getFluidAmount() <= 0) return;

    int maxTransfer = pipe.getPipeMaterial().getFluidTransferRate();

    // パイプ以外のブロックを優先ターゲットとする
    List<Direction> priorityTargets = new ArrayList<>();
    List<Direction> pipeTargets = new ArrayList<>();

    for (Direction dir : Direction.values()) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be == null) continue;
      if (!be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).isPresent()) continue;

      if (be instanceof PipeBlockEntity) {
        pipeTargets.add(dir);
      } else {
        priorityTargets.add(dir);
      }
    }

    List<Direction> targets = priorityTargets.isEmpty() ? pipeTargets : priorityTargets;
    if (targets.isEmpty()) return;

    // ラウンドロビン: 今回のターゲットを1つ選んで転送（カウンタはパイプ側で管理）
    int idx = pipe.getFluidRoundRobinIndex() % targets.size();
    Direction dir = targets.get(idx);
    pipe.advanceFluidRoundRobin(targets.size());

    BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
    if (be == null) return;

    be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
      FluidStack drained = fluidTank.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
      int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
      fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
    });
  }
}
