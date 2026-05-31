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
  public static ITriggerInternal triggerRobotSleep;
  public static ITriggerInternal triggerRobotInStation;
  public static ITriggerInternal triggerRobotLinked;
  public static ITriggerInternal triggerRobotReserved;

  public static IActionInternal actionRobotGotoStation;
  public static IActionInternal actionRobotWakeUp;
  public static IActionInternal actionRobotWorkInArea;
  public static IActionInternal actionRobotLoadUnloadArea;
  public static IActionInternal actionRobotFilter;
  public static IActionInternal actionRobotFilterTool;
  public static IActionInternal actionStationRequestItems;
  public static IActionInternal actionStationProvideItems;
  public static IActionInternal actionStationAcceptFluids;
  public static IActionInternal actionStationProvideFluids;
  public static IActionInternal actionStationForceRobot;
  public static IActionInternal actionStationForbidRobot;
  public static IActionInternal actionStationAcceptItems;
  public static IActionInternal actionStationMachineRequestItems;

  public static void init() {
    triggerRobotSleep = new TriggerRobotSleep();
    triggerRobotInStation = new TriggerRobotInStation();
    triggerRobotLinked = new TriggerRobotLinked(false);
    triggerRobotReserved = new TriggerRobotLinked(true);

    actionRobotGotoStation = new ActionRobotGotoStation();
    actionRobotWakeUp = new ActionRobotWakeUp();
    actionRobotWorkInArea = new ActionRobotWorkInArea(ActionRobotWorkInArea.AreaType.WORK);
    actionRobotLoadUnloadArea = new ActionRobotWorkInArea(ActionRobotWorkInArea.AreaType.LOAD_UNLOAD);
    actionRobotFilter = new ActionRobotFilter();
    actionRobotFilterTool = new ActionRobotFilterTool();
    actionStationRequestItems = new ActionStationRequestItems();
    actionStationProvideItems = new ActionStationProvideItems();
    actionStationAcceptFluids = new ActionStationAcceptFluids();
    actionStationProvideFluids = new ActionStationProvideFluids();
    actionStationForceRobot = new ActionStationForbidRobot(true);
    actionStationForbidRobot = new ActionStationForbidRobot(false);
    actionStationAcceptItems = new ActionStationAcceptItems();
    actionStationMachineRequestItems = new ActionStationRequestItemsMachine();

    StatementManager.registerParameterClass(StatementParameterRobot.class);
    StatementManager.registerParameterClass(StatementParameterMapLocation.class);
    StatementManager.registerActionProvider(new RobotsActionProvider());
    StatementManager.registerTriggerProvider(new RobotsTriggerProvider());
  }
}
