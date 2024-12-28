package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

@SuppressWarnings("deprecation")
public interface Entity extends INBTSerializable<CompoundTag> {
  @Override
  default CompoundTag serializeNBT(HolderLookup.Provider registryAccess) {
    return serialize(registryAccess);
  }

  CompoundTag serialize(HolderLookup.Provider registryAccess);

  @Override
  default void deserializeNBT(HolderLookup.Provider registryAccess, CompoundTag nbt) {
    deserialize(registryAccess, nbt);
  }

  void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag);
}
