package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;

public class FluidEntity implements Entity {
  private FluidStack fluid;

  @Override
  public CompoundTag serialize(HolderLookup.Provider registryAccess) {
    return fluid.writeToNBT(new CompoundTag());
  }

  @Override
  public void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag) {
    fluid = FluidStack.loadFluidStackFromNBT(tag);
  }
}
