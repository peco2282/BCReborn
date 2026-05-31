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
package com.peco2282.bcreborn.common.utils;

public final class SessionVars {

  @SuppressWarnings("rawtypes")
  private static Class openedLedger;

  /**
   * Deactivate constructor
   */
  private SessionVars() {
  }

  @SuppressWarnings("rawtypes")
  public static Class getOpenedLedger() {
    return openedLedger;
  }

  @SuppressWarnings("rawtypes")
  public static void setOpenedLedger(Class ledgerClass) {
    openedLedger = ledgerClass;
  }
}
