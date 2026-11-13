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
package com.peco2282.bcreborn.transport.test;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRobotRegistry;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.robotics.RoboticsEntityTypes;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotShutdown;
import com.peco2282.bcreborn.robotics.boards.BoardRobotLumberjack;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the robot life cycle against the original 7.1.x {@code EntityRobot} / {@code AIRobotMain}: a robot is
 * linked to the docking station it is placed on, runs its board, survives saving, and shuts down without energy.
 * <p>
 * The lumberjack board is used because without an axe it only looks for one and then sleeps, so it does not touch
 * the surrounding tests. Every test discards its robot.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class RobotGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 2, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static DockingStation<?> placeStation(GameTestHelper helper) {
    BlockState state = TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState();
    helper.setBlock(PIPE, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(PIPE)));
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(PIPE);
    // The GameTest world is reused: forget a station left behind at this position by an aborted run.
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    DockingStation<?> stale = registry.getStation(helper.absolutePos(PIPE), Direction.UP);
    if (stale != null) {
      registry.removeStation(stale);
    }
    RobotStationPluggable pluggable = new RobotStationPluggable();
    pipe.setPipePluggable(Direction.UP, pluggable);
    DockingStation<?> station = pluggable.getStation();
    check(helper, station != null, "Attaching a robot station must register a docking station");
    return station;
  }

  /** Places a robot on the station the way the robot item does (original ItemRobot.onItemUse). */
  private static RobotEntity placeRobot(GameTestHelper helper, DockingStation<?> station, int energy) {
    RedstoneBoardRobotNBT board = RoboticsRedstoneRobots.ROBOT_LUMBERJACK.get();
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    RobotEntity robot = new RobotEntity(helper.getLevel(), board);
    robot.setUniqueRobotId(registry.getNextRobotId());
    robot.setPos(station.x() + 0.5, station.y() + 0.5, station.z() + 0.5);
    robot.getBattery().receiveEnergy(energy, false);
    check(helper, helper.getLevel().addFreshEntity(robot), "The robot must spawn");
    check(helper, station.takeAsMain(robot), "A free station must accept the robot as its main robot");
    robot.dock(station);
    return robot;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void robotLinksToStationAndSurvivesSaving(GameTestHelper helper) {
    DockingStation<?> station = placeStation(helper);
    RobotEntity robot = placeRobot(helper, station, RobotEntityBase.MAX_ENERGY);

    helper.runAfterDelay(60, () -> {
      try {
        check(helper, robot.isAlive(), "The robot must stay alive");
        check(helper, robot.getLinkedStation() == station && station.robotTaking() == robot,
          "The robot and its station must be linked to each other: linked=" + robot.getLinkedStation() + " station=" + station
            + " taking=" + station.robotTaking() + " id=" + station.robotIdTaking() + "/" + robot.getRobotId() + " ai=" + robot.mainAI.getActiveAI());
        check(helper, station.isMainStation() && station.isTaken(), "The station is taken as a main station");
        check(helper, robot.getBoard() instanceof BoardRobotLumberjack, "The robot runs its board");
        check(helper, robot.getRegistry().getLoadedRobot(robot.getRobotId()) == robot, "The robot is registered");
        check(helper, !(robot.mainAI.getActiveAI() instanceof AIRobotShutdown), "A charged robot must not shut down");

        CompoundTag saved = robot.saveWithoutId(new CompoundTag());
        RobotEntity copy = new RobotEntity(RoboticsEntityTypes.ROBOT.get(), helper.getLevel());
        copy.load(saved);
        check(helper, copy.getRobotId() == robot.getRobotId(), "The robot id is saved");
        check(helper, copy.getEnergy() == robot.getEnergy(), "The energy is saved");
        check(helper, copy.getBoard() instanceof BoardRobotLumberjack, "The board is saved");

        // Original RobotRegistry.killRobot(): a removed robot frees its main station.
        robot.discard();
        check(helper, !station.isTaken(), "Removing the robot must free its station");
        helper.succeed();
      } finally {
        robot.discard();
      }
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void robotWithoutEnergyShutsDown(GameTestHelper helper) {
    DockingStation<?> station = placeStation(helper);
    RobotEntity robot = placeRobot(helper, station, 0);

    helper.runAfterDelay(40, () -> {
      try {
        check(helper, robot.isAlive(), "A robot without energy stays in the world");
        check(helper, robot.mainAI.getActiveAI() instanceof AIRobotShutdown,
          "A robot without energy must shut down, but runs " + robot.mainAI.getActiveAI());
        helper.succeed();
      } finally {
        robot.discard();
      }
    });
  }
}
