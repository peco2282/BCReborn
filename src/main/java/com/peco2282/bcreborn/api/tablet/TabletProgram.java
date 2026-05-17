package com.peco2282.bcreborn.api.tablet;

import net.minecraft.nbt.CompoundTag;

public abstract class TabletProgram {
    public void tick(float time) {
    }

    public boolean hasEnded() {
        return false;
    }

    public void receiveMessage(CompoundTag compound) {
    }
}
