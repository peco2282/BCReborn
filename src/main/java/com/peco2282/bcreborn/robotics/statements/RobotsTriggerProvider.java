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
package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.ITriggerExternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.ITriggerProvider;
import com.peco2282.bcreborn.robotics.RobotUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RobotsTriggerProvider implements ITriggerProvider {
  @Override
  public Collection<ITriggerInternal> getInternalTriggers(IStatementContainer container) {
    LinkedList<ITriggerInternal> result = new LinkedList<>();
    List<DockingStation> stations = RobotUtils.getStations(container.getTile());

    if (!stations.isEmpty()) {
      result.add(RoboticsStatements.triggerRobotSleep);
      result.add(RoboticsStatements.triggerRobotInStation);
      result.add(RoboticsStatements.triggerRobotLinked);
      result.add(RoboticsStatements.triggerRobotReserved);
    }

    return result;
  }

  @Override
  public Collection<ITriggerExternal> getExternalTriggers(Direction side, BlockEntity tile) {
    return null;
  }
}
