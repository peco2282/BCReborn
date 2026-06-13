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
package com.peco2282.bcreborn.api.blueprints;

import java.io.Serial;

/**
 * Exception thrown when a block, item, or entity mapping cannot be found in the registry.
 */
public class MappingNotFoundException extends Exception {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new MappingNotFoundException with the specified message.
   *
   * @param msg The detail message.
   */
  public MappingNotFoundException(String msg) {
    super(msg);
  }
}