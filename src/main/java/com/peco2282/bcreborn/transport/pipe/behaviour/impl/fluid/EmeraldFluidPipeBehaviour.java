package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

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

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;
    if (pipe.getTicksSincePull() < EXTRACT_COOLDOWN) return;

    Direction extractDir = pipe.getExtractionSide();
    BlockPos inventoryPos = pos.relative(extractDir);
    BlockEntity invBe = level.getBlockEntity(inventoryPos);

    if (invBe == null || invBe instanceof PipeBlockEntity) return;

    int extractAmount = pipe.getPipeMaterial().getFluidTransferRate();

    LazyOptional<IFluidHandler> cap = invBe.getCapability(ForgeCapabilities.FLUID_HANDLER, extractDir.getOpposite());
    if (!cap.isPresent()) return;

    IFluidHandler handler = cap.orElseThrow(IllegalStateException::new);

    // フィルタースロットから許可流体リストを構築
    List<FluidStack> allowedFluids = buildAllowedFluids(pipe, extractDir);

    FluidStack drained;
    if (allowedFluids.isEmpty()) {
      // フィルターなし: タンクに既存流体があれば同種のみ、なければ何でも抽出
      FluidStack existing = pipe.getFluidTank() != null ? pipe.getFluidTank().getFluid() : FluidStack.EMPTY;
      if (!existing.isEmpty()) {
        drained = handler.drain(new FluidStack(existing, extractAmount), IFluidHandler.FluidAction.EXECUTE);
      } else {
        drained = handler.drain(extractAmount, IFluidHandler.FluidAction.EXECUTE);
      }
    } else {
      // フィルターあり: 許可された流体のみ抽出
      drained = FluidStack.EMPTY;
      for (FluidStack allowed : allowedFluids) {
        FluidStack candidate = handler.drain(new FluidStack(allowed.getFluid(), extractAmount), IFluidHandler.FluidAction.SIMULATE);
        if (!candidate.isEmpty()) {
          drained = handler.drain(new FluidStack(allowed.getFluid(), extractAmount), IFluidHandler.FluidAction.EXECUTE);
          break;
        }
      }
    }

    if (!drained.isEmpty() && pipe.getFluidTank() != null) {
      pipe.getFluidTank().fill(drained, IFluidHandler.FluidAction.EXECUTE);
      pipe.resetTicksSincePull();
    }
  }

  /**
   * 抽出方向のフィルタースロットからバケツ等を読み取り、許可流体リストを返す。
   */
  private List<FluidStack> buildAllowedFluids(PipeBlockEntity pipe, Direction extractDir) {
    List<FluidStack> result = new ArrayList<>();
    var filterInv = pipe.getFilter(extractDir);
    if (filterInv == null) return result;

    for (int i = 0; i < filterInv.getContainerSize(); i++) {
      ItemStack filterItem = filterInv.getItem(i);
      if (filterItem.isEmpty()) continue;
      Optional<FluidStack> fluid = FluidUtil.getFluidContained(filterItem);
      fluid.ifPresent(result::add);
    }
    return result;
  }
}
