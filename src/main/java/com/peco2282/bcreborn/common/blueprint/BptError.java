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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.core.BCLog;

public class BptError extends Exception {
  private static final long serialVersionUID = 3579188081467555542L;

  public BptError(String str) {
    super(str);
    BCLog.logger.debug("BLUEPRINT ERROR: " + str);
  }
}
