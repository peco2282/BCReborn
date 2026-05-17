package com.peco2282.bcreborn.api.library;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class LibraryTypeHandlerNBT extends LibraryTypeHandler {
    public LibraryTypeHandlerNBT(String extension) {
        super(extension);
    }

    public abstract ItemStack load(ItemStack stack, CompoundTag nbt);

    public abstract boolean store(ItemStack stack, CompoundTag nbt);
}
