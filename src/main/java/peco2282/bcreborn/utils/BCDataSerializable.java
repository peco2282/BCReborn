package peco2282.bcreborn.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.io.Serializable;

public interface BCDataSerializable extends Serializable {
  CompoundTag serialize(HolderLookup.Provider provider);
  void deserialize(CompoundTag tag, HolderLookup.Provider provider);
}
