package com.peco2282.bcreborn.api.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBT extends INBTSerializable<CompoundTag> {
  void read(CompoundTag nbt);

  void write(CompoundTag nbt);

  @Override
  default CompoundTag serializeNBT() {
    CompoundTag nbt = new CompoundTag();
    write(nbt);
    return nbt;
  }

  @Override
  default void deserializeNBT(CompoundTag nbt) {
    read(nbt);
  }
}
