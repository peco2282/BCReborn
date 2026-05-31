/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
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
