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

import java.util.HashMap;
import java.util.Map;

public final class TabletAPI {
  private static final Map<String, TabletProgramFactory> programs = new HashMap<>();

  private TabletAPI() {

  }

  public static void registerProgram(TabletProgramFactory factory) {
    programs.put(factory.getName(), factory);
  }

  public static TabletProgramFactory getProgram(String name) {
    return programs.get(name);
  }
}
