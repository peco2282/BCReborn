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

import com.peco2282.bcreborn.core.statements.DefaultTriggerProvider;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.robotics.ai.AIRobotLoad;
import com.peco2282.bcreborn.robotics.statements.RoboticsStatements;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
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
    return placeStation(helper, PIPE, PipeMaterial.COBBLESTONE);
  }

  private static DockingStation<?> placeStation(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    return placeStation(helper, pos, PipeType.ITEM, material);
  }

  private static DockingStation<?> placeStation(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    // The GameTest world is reused: forget a station left behind at this position by an aborted run.
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    DockingStation<?> stale = registry.getStation(helper.absolutePos(pos), Direction.UP);
    if (stale != null) {
      registry.removeStation(stale);
    }
    RobotStationPluggable pluggable = new RobotStationPluggable();
    pipe.setPipePluggable(Direction.UP, pluggable);
    DockingStation<?> station = pluggable.getStation();
    check(helper, station != null, "Attaching a robot station must register a docking station");
    return station;
  }

  /** A gate on NORTH of the station's pipe whose single rule is active as long as there is no redstone signal. */
  private static void stationAction(GameTestHelper helper, BlockPos pos, IStatement action) {
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND, Direction.NORTH);
    pipe.setPipePluggable(Direction.NORTH, new GatePluggable(gate));
    gate.setTrigger(0, DefaultTriggerProvider.triggerRedstoneInactive);
    gate.setAction(0, action);
  }

  /** Places a robot on the station the way the robot item does (original ItemRobot.onItemUse). */
  private static RobotEntity placeRobot(GameTestHelper helper, DockingStation<?> station, int energy) {
    return placeRobot(helper, station, energy, RoboticsRedstoneRobots.ROBOT_LUMBERJACK.get());
  }

  private static RobotEntity placeRobot(GameTestHelper helper, DockingStation<?> station, int energy,
                                        RedstoneBoardRobotNBT board) {
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

  /**
   * Original BoardRobotCarrier: the robot loads at a station that provides items (a wooden pipe on an inventory
   * with a "provide items" gate action) and unloads at a station that accepts items.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "quarry_13x8x13", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 1200)
  public void carrierMovesItemsBetweenStations(GameTestHelper helper) {
    BlockPos source = new BlockPos(3, 2, 3);
    BlockPos target = new BlockPos(8, 2, 3);
    helper.setBlock(source.south(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(target.south(), Blocks.CHEST.defaultBlockState());
    Container from = (Container) helper.getBlockEntity(source.south());
    Container to = (Container) helper.getBlockEntity(target.south());
    from.setItem(0, new ItemStack(Items.IRON_INGOT, 5));

    DockingStation<?> home = placeStation(helper, source, PipeMaterial.WOOD);
    placeStation(helper, target, PipeMaterial.COBBLESTONE);
    check(helper, home.getItemInput() == from, "A wooden pipe station takes items from the pipe's inventory");
    check(helper, !AIRobotLoadProbe.canLoad(home), "Without a provide action nothing may be taken");
    stationAction(helper, source, RoboticsStatements.actionStationProvideItems);
    stationAction(helper, target, RoboticsStatements.actionStationAcceptItems);

    // The gates need a few ticks to resolve their actions; a carrier that finds nothing to do sleeps for a minute.
    RobotEntity[] holder = new RobotEntity[1];
    helper.runAfterDelay(20, () -> {
      check(helper, AIRobotLoadProbe.canLoad(home), "With an active provide action the station offers its items");
      holder[0] = placeRobot(helper, home, RobotEntityBase.MAX_ENERGY, RoboticsRedstoneRobots.ROBOT_CARRIER.get());
    });
    helper.onEachTick(() -> {
      if (helper.getTick() == 1190 && holder[0] != null) {
        holder[0].discard();
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity robot = holder[0];
      helper.assertTrue(robot != null, "The robot is not placed yet");
      int moved = 0;
      for (int i = 0; i < to.getContainerSize(); i++) {
        if (to.getItem(i).is(Items.IRON_INGOT)) {
          moved += to.getItem(i).getCount();
        }
      }
      helper.assertTrue(moved == 5 && from.getItem(0).isEmpty(),
        "The carrier must move all items to the accepting station, moved " + moved + ", source " + from.getItem(0)
          + ", robot holds " + robot.getItem(0) + " at " + robot.blockPosition() + " docked " + robot.getDockingStation()
          + " running " + robot.mainAI.getActiveAI() + " energy " + robot.getEnergy());
      robot.discard();
    });
  }

  /**
   * Original RobotStationPluggable.receiveEnergy(): a station on a power pipe charges the robot docked on it,
   * and a robot below its safety energy stays there to recharge (AIRobotRecharge).
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void powerPipeStationChargesRobot(GameTestHelper helper) {
    BlockPos engine = new BlockPos(0, 2, 1);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState()
      .setValue(EngineBlock.FACING, Direction.EAST));
    helper.setBlock(new BlockPos(1, 2, 1), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    DockingStation<?> station = placeStation(helper, new BlockPos(2, 2, 1), PipeType.ENERGY, PipeMaterial.GOLD);
    check(helper, station.providesPower(), "A station on a power pipe provides power");

    int start = RobotEntityBase.SAFETY_ENERGY / 2;
    RobotEntity robot = placeRobot(helper, station, start);
    helper.onEachTick(() -> {
      if (helper.getTick() == 390) {
        robot.discard();
      }
    });
    helper.succeedWhen(() -> {
      helper.assertTrue(robot.getEnergy() > start + 2000,
        "The docked robot must be charged, has " + robot.getEnergy() + " running " + robot.mainAI.getActiveAI());
      helper.assertTrue(robot.getDockingStation() == station, "A robot that recharges stays on its power station");
      robot.discard();
    });
  }

  /**
   * Original BoardRobotFluidCarrier: the robot fills its tank at a station that provides fluids (a wooden fluid
   * pipe on a tank) and empties it into the pipe of a station that accepts fluids.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "quarry_13x8x13", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 1200)
  public void fluidCarrierMovesFluidBetweenStations(GameTestHelper helper) {
    BlockPos source = new BlockPos(3, 2, 8);
    BlockPos target = new BlockPos(8, 2, 8);
    helper.setBlock(source.south(), FactoryBlocks.TANK.get().defaultBlockState());
    helper.setBlock(target.south(), FactoryBlocks.TANK.get().defaultBlockState());
    TankBlockEntity from = (TankBlockEntity) helper.getBlockEntity(source.south());
    TankBlockEntity to = (TankBlockEntity) helper.getBlockEntity(target.south());
    from.tank.fill(new FluidStack(Fluids.WATER, 8000), IFluidHandler.FluidAction.EXECUTE);

    DockingStation<?> home = placeStation(helper, source, PipeType.FLUID, PipeMaterial.WOOD);
    ((PipeBlockEntity) helper.getBlockEntity(source)).setExtractionSide(Direction.SOUTH);
    DockingStation<?> drop = placeStation(helper, target, PipeType.FLUID, PipeMaterial.COBBLESTONE);
    check(helper, home.getFluidInput() != null, "A wooden fluid pipe station takes fluid from the pipe's tank");
    check(helper, drop.getFluidOutput() != null && drop.getFluidInput() == null,
      "Any fluid pipe station accepts fluid, only a wooden one provides it");
    stationAction(helper, source, RoboticsStatements.actionStationProvideFluids);
    stationAction(helper, target, RoboticsStatements.actionStationAcceptFluids);

    RobotEntity[] holder = new RobotEntity[1];
    helper.runAfterDelay(20, () -> {
      holder[0] = placeRobot(helper, home, RobotEntityBase.MAX_ENERGY, RoboticsRedstoneRobots.ROBOT_FLUID_CARRIER.get());
      RobotEntity robot = holder[0];
      check(helper, robot.getTanks() == 1 && robot.getTankCapacity(0) == 4000 && robot.getFluidInTank(0).isEmpty(),
        "A robot has one empty tank of four buckets");
    });
    // Every place the water can be: the two tanks, the two pipes and the robot. It must add up on every tick.
    PipeBlockEntity sourcePipe = (PipeBlockEntity) helper.getBlockEntity(source);
    PipeBlockEntity targetPipe = (PipeBlockEntity) helper.getBlockEntity(target);
    helper.onEachTick(() -> {
      RobotEntity robot = holder[0];
      int carried = robot != null ? robot.getFluidInTank(0).getAmount() : 0;
      int total = from.tank.getFluidAmount() + pipeFluid(sourcePipe) + carried + pipeFluid(targetPipe) + to.tank.getFluidAmount();
      check(helper, total == 8000, "Moving water must neither lose nor create any, total " + total + " (source tank "
        + from.tank.getFluidAmount() + ", source pipe " + pipeFluid(sourcePipe) + ", robot " + carried + ", target pipe "
        + pipeFluid(targetPipe) + ", target tank " + to.tank.getFluidAmount() + ") at tick " + helper.getTick());
      check(helper, carried <= 4000, "The robot's tank holds at most four buckets, holds " + carried);
      if (helper.getTick() == 1190 && robot != null) {
        robot.discard();
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity robot = holder[0];
      helper.assertTrue(robot != null, "The robot is not placed yet");
      int delivered = to.tank.getFluidAmount() + pipeFluid(targetPipe);
      helper.assertTrue(delivered >= 4000,
        "The fluid carrier must bring at least a full robot tank to the accepting station, delivered " + delivered
          + ", robot holds " + robot.getFluidInTank(0).getAmount()
          + " at " + robot.blockPosition() + " running " + robot.mainAI.getActiveAI());
      robot.discard();
    });
  }

  private static int pipeFluid(PipeBlockEntity pipe) {
    return pipe.getFluidTank() != null ? pipe.getFluidTank().getFluidAmount() : 0;
  }

  /**
   * The charging change made {@code EnergyTransportModule} collect demand from robot stations as well. A station
   * and an ordinary machine on the same power pipe must both be supplied, and neither may go over its capacity.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void powerPipeStationSharesNetworkWithMachine(GameTestHelper helper) {
    BlockPos engine = new BlockPos(0, 2, 1);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState()
      .setValue(EngineBlock.FACING, Direction.EAST));
    helper.setBlock(new BlockPos(1, 2, 1), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    BlockPos machinePos = new BlockPos(2, 2, 2);
    helper.setBlock(machinePos, FactoryBlocks.REFINERY.get().defaultBlockState());
    RefineryBlockEntity machine = (RefineryBlockEntity) helper.getBlockEntity(machinePos);
    DockingStation<?> station = placeStation(helper, new BlockPos(2, 2, 1), PipeType.ENERGY, PipeMaterial.GOLD);
    check(helper, station.providesPower(), "A station on a power pipe provides power");

    int start = RobotEntityBase.SAFETY_ENERGY / 2;
    RobotEntity robot = placeRobot(helper, station, start);
    helper.onEachTick(() -> {
      check(helper, robot.getEnergy() <= RobotEntityBase.MAX_ENERGY, "A robot must not be charged over its capacity, has " + robot.getEnergy());
      check(helper, machine.getEnergyStored() <= machine.getMaxEnergyStored(),
        "A machine must not be charged over its capacity, has " + machine.getEnergyStored());
      if (helper.getTick() == 390) {
        robot.discard();
      }
    });
    helper.succeedWhen(() -> {
      helper.assertTrue(robot.getEnergy() > start + 2000,
        "The docked robot must be charged next to a machine, has " + robot.getEnergy() + " running " + robot.mainAI.getActiveAI());
      helper.assertTrue(machine.getEnergyStored() > 0, "The machine must still be supplied next to a robot station");
      helper.assertTrue(robot.getDockingStation() == station, "A robot that recharges stays on its power station");
      robot.discard();
    });
  }

  /** Small probe so that the test reads like the rule it checks. */
  private static final class AIRobotLoadProbe {
    static boolean canLoad(DockingStation<?> station) {
      return !AIRobotLoad.takeSingle(station, stack -> true, false).isEmpty();
    }
  }
}
