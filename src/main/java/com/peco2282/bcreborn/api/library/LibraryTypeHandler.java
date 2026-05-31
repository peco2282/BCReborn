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
package com.peco2282.bcreborn.api.library;

import net.minecraft.world.item.ItemStack;

public abstract class LibraryTypeHandler {
    public enum HandlerType {
        LOAD, STORE
    }

    private final String extension;

    public LibraryTypeHandler(String extension) {
        this.extension = extension;
    }

    public abstract boolean isHandler(ItemStack stack, HandlerType type);

    public boolean isInputExtension(String ext) {
        return extension.equals(ext);
    }

    public String getOutputExtension() {
        return extension;
    }

    public abstract int getTextColor();

    public abstract String getName(ItemStack stack);
}
