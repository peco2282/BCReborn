package com.peco2282.bcreborn.energy.fluids;

import com.peco2282.bcreborn.energy.FluidsEnergy;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class OilFluid extends ForgeFlowingFluid {

  protected OilFluid(Properties properties) {
    super(properties);
  }

  @Override
  public Fluid getFlowing() {
    return FluidsEnergy.OIL_FLOWING.get();
  }

  @Override
  public Fluid getSource() {
    return FluidsEnergy.OIL_SOURCE.get();
  }

  @Override
  public boolean isSame(Fluid fluid) {
    return fluid == FluidsEnergy.OIL_SOURCE.get() || fluid == FluidsEnergy.OIL_FLOWING.get();
  }

  public static class Source extends OilFluid {
    public Source(Properties properties) {
      super(properties);
    }

    @Override
    public int getAmount(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }

  public static class Flowing extends OilFluid {
    public Flowing(Properties properties) {
      super(properties);
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
      super.createFluidStateDefinition(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState state) {
      return state.getValue(LEVEL);
    }

    @Override
    public boolean isSource(FluidState state) {
      return false;
    }
  }
}
