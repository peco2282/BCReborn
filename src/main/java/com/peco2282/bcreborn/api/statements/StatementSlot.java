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
package com.peco2282.bcreborn.api.statements;

public class StatementSlot {
  public IStatement statement;
  public IStatementParameter[] parameters;

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof StatementSlot s)) {
      return false;
    }
    if (s.statement != statement || parameters.length != s.parameters.length) {
      return false;
    }
    for (int i = 0; i < parameters.length; i++) {
      IStatementParameter p1 = parameters[i];
      IStatementParameter p2 = s.parameters[i];
      if (p1 == null && p2 != null) {
        return false;
      }
      if (!(p1.equals(p2))) {
        return false;
      }
    }
    return true;
  }
}
