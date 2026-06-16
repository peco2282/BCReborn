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
package com.peco2282.bcreborn.api.transport;

public record StripeHandlerEntry(
        IStripesHandler handler,
        int priority
) {
    public StripeHandlerEntry(IStripesHandler handler) {
        this(handler, 0);
    }

    public static StripeHandlerEntry of(IStripesHandler handler) {
        return new StripeHandlerEntry(handler);
    }

    public static StripeHandlerEntry of(IStripesHandler handler, int priority) {
        return new StripeHandlerEntry(handler, priority);
    }
}
