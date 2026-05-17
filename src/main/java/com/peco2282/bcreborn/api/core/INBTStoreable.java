package com.peco2282.bcreborn.api.core;

import net.minecraft.nbt.CompoundTag;

public interface INBTStoreable {
	void readFromNBT(CompoundTag tag);
	void writeToNBT(CompoundTag tag);
}
