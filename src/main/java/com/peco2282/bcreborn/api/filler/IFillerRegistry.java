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
package com.peco2282.bcreborn.api.filler;

import java.util.Collection;

public interface IFillerRegistry {
  void addPattern(IFillerPattern pattern);

  IFillerPattern getPattern(String patternName);

  IFillerPattern getNextPattern(IFillerPattern currentPattern);

  IFillerPattern getPreviousPattern(IFillerPattern currentPattern);

  Collection<IFillerPattern> getPatterns();
}
