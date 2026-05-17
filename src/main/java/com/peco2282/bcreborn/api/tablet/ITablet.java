package com.peco2282.bcreborn.api.tablet;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;

public interface ITablet {
    Dist getSide();

    void refreshScreen(TabletBitmap data);

    int getScreenWidth();

    int getScreenHeight();

    void launchProgram(String name);

    void sendMessage(CompoundTag compound);
}
