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
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;

public class AIRobotGotoStationAndLoad extends AIRobot {

  private IStackFilter filter;
  private int quantity;

  public AIRobotGotoStationAndLoad(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public AIRobotGotoStationAndLoad(EntityRobotBase iRobot, IStackFilter iFilter, int iQuantity) {
    this(iRobot);

    filter = iFilter;
    quantity = iQuantity;
  }

  @Override
  public void start() {
    startDelegateAI(new AIRobotGotoStationToLoad(robot, filter, quantity));
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationToLoad) {
      if (filter != null && ai.success()) {
        startDelegateAI(new AIRobotLoad(robot, filter, quantity));
      } else {
        setSuccess(false);
        terminate();
      }
    } else if (ai instanceof AIRobotLoad) {
      setSuccess(ai.success());
      terminate();
    }
  }
}
