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

import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.ITriggerInternal;
import com.peco2282.bcreborn.api.statements.StatementManager;

public class RoboticsStatements {
  public final static ITriggerInternal triggerRobotSleep = new TriggerRobotSleep();
  public final static ITriggerInternal triggerRobotInStation = new TriggerRobotInStation();
  public final static ITriggerInternal triggerRobotLinked = new TriggerRobotLinked(false);
  public final static ITriggerInternal triggerRobotReserved = new TriggerRobotLinked(true);

  public static final IActionInternal actionRobotGotoStation = new ActionRobotGotoStation();
  public static final IActionInternal actionRobotWakeUp = new ActionRobotWakeUp();
  public static final IActionInternal actionRobotWorkInArea = new ActionRobotWorkInArea(ActionRobotWorkInArea.AreaType.WORK);
  public static final IActionInternal actionRobotLoadUnloadArea = new ActionRobotWorkInArea(ActionRobotWorkInArea.AreaType.LOAD_UNLOAD);
  public static final IActionInternal actionRobotFilter = new ActionRobotFilter();
  public static final IActionInternal actionRobotFilterTool = new ActionRobotFilterTool();
  public static final IActionInternal actionStationRequestItems = new ActionStationRequestItems();
  public static final IActionInternal actionStationProvideItems = new ActionStationProvideItems();
  public static final IActionInternal actionStationAcceptFluids = new ActionStationAcceptFluids();
  public static final IActionInternal actionStationProvideFluids = new ActionStationProvideFluids();
  public static final IActionInternal actionStationForceRobot = new ActionStationForbidRobot(true);
  public static final IActionInternal actionStationForbidRobot = new ActionStationForbidRobot(false);
  public static final IActionInternal actionStationAcceptItems = new ActionStationAcceptItems();
  public static final IActionInternal actionStationMachineRequestItems = new ActionStationRequestItemsMachine();

  public static void init() {
    StatementManager.registerParameter(StatementParameterRobot.INSTANCE, StatementParameterRobot.CODEC);
    StatementManager.registerParameter(StatementParameterMapLocation.INSTANCE, StatementParameterMapLocation.CODEC);
    StatementManager.registerActionProvider(RobotsActionProvider.INSTANCE);
    StatementManager.registerTriggerProvider(RobotsTriggerProvider.INSTANCE);
  }
}
