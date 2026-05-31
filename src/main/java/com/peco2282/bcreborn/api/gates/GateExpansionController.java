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
package com.peco2282.bcreborn.api.gates;

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public abstract class GateExpansionController {
  public final IGateExpansion type;
  public final BlockEntity pipeTile;

  public GateExpansionController(IGateExpansion type, BlockEntity pipeTile) {
    this.pipeTile = pipeTile;
    this.type = type;
  }

  public IGateExpansion getType() {
    return type;
  }

  public boolean isActive() {
    return false;
  }

  public void tick(IGate gate) {
  }

  public void startResolution() {
  }

  public boolean resolveAction(IStatement action, int count) {
    return false;
  }

  public boolean isTriggerActive(IStatement trigger, IStatementParameter[] parameters) {
    return false;
  }

  public void addTriggers(List<ITriggerInternal> list) {
  }

  public void addActions(List<IActionInternal> list) {
  }

  public void writeToNBT(CompoundTag nbt) {
  }

  public void readFromNBT(CompoundTag nbt) {
  }
}
