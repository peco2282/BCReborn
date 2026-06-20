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

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class LibraryAPI {
  private static final Set<LibraryTypeHandler> handlers = new HashSet<>();

  private LibraryAPI() {

  }

  public static Set<LibraryTypeHandler> getHandlerSet() {
    return handlers;
  }

  public static void registerHandler(LibraryTypeHandler handler) {
    handlers.add(handler);
  }

  @Nullable
  public static LibraryTypeHandler getHandlerFor(String extension) {
    for (LibraryTypeHandler h : handlers) {
      if (h.isInputExtension(extension)) {
        return h;
      }
    }
    return null;
  }
}
