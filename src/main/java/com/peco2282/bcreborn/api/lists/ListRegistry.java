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
package com.peco2282.bcreborn.api.lists;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListRegistry {
    public static final List<Class<? extends Item>> itemClassAsType = new ArrayList<>();
    private static final List<ListMatchHandler> handlers = new ArrayList<>();

    private ListRegistry() {
    }

    public static void registerHandler(ListMatchHandler h) {
        if (h != null) {
            handlers.add(h);
        }
    }

    public static List<ListMatchHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }
}
