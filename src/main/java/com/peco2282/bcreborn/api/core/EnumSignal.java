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
package com.peco2282.bcreborn.api.core;

import java.util.Locale;

public enum EnumSignal {
    RED,
    BLUE,
    GREEN,
    YELLOW;

    public static final EnumSignal[] VALUES = values();

    public String getTag() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getName() {
        String name = name().toLowerCase(Locale.ENGLISH);
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    public EnumSignal getNext() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public EnumSignal getPrevious() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public static EnumSignal fromId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return RED;
        }
        return VALUES[id];
    }
}
