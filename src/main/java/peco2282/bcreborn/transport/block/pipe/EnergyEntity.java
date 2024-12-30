package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class EnergyEntity implements Entity {
  @Override
  public CompoundTag serialize(HolderLookup.Provider registryAccess) {
    return new CompoundTag();
  }

  @Override
  public void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag) {
  }
}
