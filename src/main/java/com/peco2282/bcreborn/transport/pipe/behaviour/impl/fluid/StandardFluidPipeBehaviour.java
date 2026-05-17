package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class StandardFluidPipeBehaviour implements FluidPipeBehaviour {
  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    PipeMaterial thisMat = pipe.getPipeMaterial();
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      PipeMaterial otherMat = otherPipe.getPipeMaterial();
      if ((thisMat == PipeMaterial.STONE && otherMat == PipeMaterial.COBBLESTONE) ||
          (thisMat == PipeMaterial.COBBLESTONE && otherMat == PipeMaterial.STONE)) {
        return false;
      }
      if (thisMat == PipeMaterial.SANDSTONE && otherMat != PipeMaterial.SANDSTONE) {
        return false;
      }
      return otherMat != PipeMaterial.SANDSTONE || thisMat == PipeMaterial.SANDSTONE;
    }
    return true;
  }

  @Override
  public void transferFluid(PipeBlockEntity pipe) {
    var fluidTank = pipe.getFluidTank();
    if (fluidTank == null || fluidTank.getFluidAmount() <= 0) return;

    // このパイプのmaterial転送上限を適用
    int maxTransfer = pipe.getPipeMaterial().getFluidTransferRate();

    List<Direction> targets = new ArrayList<>();
    for (Direction dir : Direction.values()) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).isPresent()) {
        targets.add(dir);
      }
    }
    if (targets.isEmpty()) return;

    int available = Math.min(fluidTank.getFluidAmount(), maxTransfer);
    int toTransfer = available / targets.size();
    if (toTransfer <= 0) toTransfer = 1;

    for (Direction dir : targets) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be == null) continue;
      final int amount = toTransfer;
      be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
        FluidStack drained = fluidTank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
      });
    }
  }
}
