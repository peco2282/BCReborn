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
import com.peco2282.bcreborn.api.robots.ResourceIdRequest;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.api.transport.PowerMode;
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.core.statements.DefaultTriggerProvider;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsEntityTypes;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotLoad;
import com.peco2282.bcreborn.robotics.ai.AIRobotLoadFluids;
import com.peco2282.bcreborn.robotics.ai.AIRobotShutdown;
import com.peco2282.bcreborn.robotics.ai.AIRobotUnloadFluids;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.robotics.statements.RoboticsStatements;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Boundary and failure cases of the robots (ROADMAP 8-2): every station side, the robot's and the target's
 * capacity, a broken station, a partly fulfilled request across a reload, a limited power supply, and boards that
 * have nothing they may do. Every check that matters is about nothing being lost or created.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class RobotEdgeCaseGameTests {
  private static final String BIG = "quarry_13x8x13";
  private static final String SMALL = "empty_3x3";
  private static final BlockPos HOME = new BlockPos(3, 3, 3);
  private static final BlockPos JOB = new BlockPos(8, 3, 3);
  private static final int TIMEOUT = 1500;

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static ItemStack areaMap(GameTestHelper helper) {
    BlockPos min = helper.absolutePos(new BlockPos(0, 1, 0));
    BlockPos max = helper.absolutePos(new BlockPos(12, 7, 12));
    ItemStack map = new ItemStack(CoreItems.MAP_LOCATION.get());
    CompoundTag tag = map.getOrCreateTag();
    tag.putByte(MapLocationItem.TAG_KIND, MapLocationItem.KIND_AREA);
    tag.putInt("xMin", min.getX());
    tag.putInt("yMin", min.getY());
    tag.putInt("zMin", min.getZ());
    tag.putInt("xMax", max.getX());
    tag.putInt("yMax", max.getY());
    tag.putInt("zMax", max.getZ());
    return map;
  }

  /**
   * A robot station on {@code side} of a pipe at {@code pos}, with a gate on {@code gateSide} whose rules are all
   * active while there is no redstone signal. In the big structure the first rule keeps robots inside it.
   */
  private static DockingStation<?> placeStation(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material,
                                                Direction side, Direction gateSide, IStatement... actions) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    DockingStation<?> stale = registry.getStation(helper.absolutePos(pos), side);
    if (stale != null) {
      registry.removeStation(stale);
    }
    RobotStationPluggable pluggable = new RobotStationPluggable();
    pipe.setPipePluggable(side, pluggable);
    DockingStation<?> station = pluggable.getStation();
    check(helper, station != null, "Attaching a robot station must register a docking station");

    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.EMERALD, GateDefinition.GateLogic.AND, gateSide);
    pipe.setPipePluggable(gateSide, new GatePluggable(gate));
    int slot = 0;
    // Stations in the big structure sit at y >= 3 (above its two-block floor), those in the small one at y = 2.
    if (pos.getY() >= 3) {
      gate.setTrigger(slot, DefaultTriggerProvider.triggerRedstoneInactive);
      gate.setAction(slot, RoboticsStatements.actionRobotWorkInArea);
      gate.setActionParameter(slot, 0, new StatementParameterItemStack(areaMap(helper)));
      slot++;
    }
    for (IStatement action : actions) {
      gate.setTrigger(slot, DefaultTriggerProvider.triggerRedstoneInactive);
      gate.setAction(slot, action);
      slot++;
    }
    return station;
  }

  private static Gate gate(GameTestHelper helper, BlockPos pos, Direction gateSide) {
    return ((GatePluggable) ((PipeBlockEntity) helper.getBlockEntity(pos)).getPipePluggable(gateSide)).realGate;
  }

  private static RobotEntity spawnRobot(GameTestHelper helper, DockingStation<?> station, RedstoneBoardRobotNBT board, int energy) {
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

  /** A robot that is not in the world: its inventory and tank are used directly, no AI runs. */
  private static RobotEntity looseRobot(GameTestHelper helper) {
    return new RobotEntity(helper.getLevel(), RoboticsRedstoneRobots.ROBOT_CARRIER.get());
  }

  private static int count(Container container, Item item) {
    int count = 0;
    for (int i = 0; i < container.getContainerSize(); i++) {
      if (container.getItem(i).is(item)) {
        count += container.getItem(i).getCount();
      }
    }
    return count;
  }

  private static int dropped(GameTestHelper helper, Item item) {
    int count = 0;
    AABB area = new AABB(helper.absolutePos(BlockPos.ZERO), helper.absolutePos(new BlockPos(12, 7, 12))).inflate(2);
    for (ItemEntity entity : helper.getLevel().getEntitiesOfClass(ItemEntity.class, area)) {
      if (entity.getItem().is(item)) {
        count += entity.getItem().getCount();
      }
    }
    return count;
  }

  private static int pipeFluid(PipeBlockEntity pipe) {
    return pipe.getFluidTank() != null ? pipe.getFluidTank().getFluidAmount() : 0;
  }

  /**
   * Places a board robot at tick 20 after {@code setup} has prepared it, discards it at the end, and runs
   * {@code check} at {@code checkTick} (for boards that must not do anything).
   */
  private static void runIdleBoard(GameTestHelper helper, DockingStation<?> home, RedstoneBoardRobotNBT board, int energy,
                                   Consumer<RobotEntity> setup, int checkTick, Consumer<RobotEntity> check) {
    RobotEntity[] holder = new RobotEntity[1];
    helper.runAfterDelay(20, () -> {
      holder[0] = spawnRobot(helper, home, board, energy);
      setup.accept(holder[0]);
    });
    helper.runAtTickTime(checkTick, () -> {
      try {
        check.accept(holder[0]);
        helper.succeed();
      } finally {
        holder[0].discard();
      }
    });
  }

  // ---------------------------------------------------------------- station sides

  /**
   * A station works on every side of its pipe and with its gate on any other side: E3 / E5 only used a station on
   * top and a gate on the north. The chest is always south of the wooden pipe.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void stationWorksOnEverySide(GameTestHelper helper) {
    Direction[][] rigs = {
      {Direction.UP, Direction.NORTH}, {Direction.UP, Direction.EAST}, {Direction.UP, Direction.WEST},
      {Direction.UP, Direction.DOWN}, {Direction.DOWN, Direction.UP}, {Direction.EAST, Direction.NORTH},
      {Direction.WEST, Direction.NORTH}, {Direction.NORTH, Direction.UP}};
    List<DockingStation<?>> stations = new ArrayList<>();
    for (int i = 0; i < rigs.length; i++) {
      BlockPos pipe = new BlockPos(2 + 3 * (i % 4), 4, 3 + 5 * (i / 4));
      helper.setBlock(pipe.south(), Blocks.CHEST.defaultBlockState());
      ((Container) helper.getBlockEntity(pipe.south())).setItem(0, new ItemStack(Items.IRON_INGOT, 5));
      stations.add(placeStation(helper, pipe, PipeType.ITEM, PipeMaterial.WOOD, rigs[i][0], rigs[i][1],
        RoboticsStatements.actionStationProvideItems));
    }
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    helper.runAfterDelay(20, () -> {
      List<RobotEntity> robots = new ArrayList<>();
      try {
        for (int i = 0; i < rigs.length; i++) {
          DockingStation<?> station = stations.get(i);
          String rig = "station " + rigs[i][0] + " / gate " + rigs[i][1];
          check(helper, station.side() == rigs[i][0] && registry.getStation(station.pos(), station.side()) == station,
            rig + ": the station must be registered on its own side");
          check(helper, !AIRobotLoad.takeSingle(station, stack -> true, false).isEmpty(),
            rig + ": the gate's provide action must reach the station");
          RobotEntity robot = spawnRobot(helper, station, RoboticsRedstoneRobots.ROBOT_LUMBERJACK.get(), RobotEntityBase.MAX_ENERGY);
          robots.add(robot);
          check(helper, robot.getLinkedStation() == station && robot.getDockingStation() == station,
            rig + ": the robot must link and dock to the station");
        }
        helper.succeed();
      } finally {
        robots.forEach(Entity::discard);
      }
    });
  }

  // ---------------------------------------------------------------- capacity

  /** Original AIRobotLoad.load(): a robot takes only what fits and what was asked for; the rest stays in the chest. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void loadingStopsAtRobotCapacity(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(pos.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 5));
    DockingStation<?> station = placeStation(helper, pos, PipeType.ITEM, PipeMaterial.WOOD, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationProvideItems);

    helper.runAfterDelay(20, () -> {
      RobotEntity robot = looseRobot(helper);
      for (int i = 0; i < robot.getContainerSize(); i++) {
        robot.setItem(i, new ItemStack(Items.STONE, 64));
      }
      check(helper, !AIRobotLoad.load(robot, station, stack -> true, AIRobotLoad.ANY_QUANTITY, true),
        "A full robot must not load anything");
      check(helper, count(chest, Items.IRON_INGOT) == 5, "A full robot must leave the chest alone, left " + count(chest, Items.IRON_INGOT));

      // Original EntityRobot.isItemValidForSlot(): a stack goes into a slot only if all of it fits there.
      robot.setItem(0, new ItemStack(Items.IRON_INGOT, 62));
      check(helper, !AIRobotLoad.load(robot, station, stack -> true, AIRobotLoad.ANY_QUANTITY, true),
        "Five ingots do not fit onto 62, so nothing is loaded");
      check(helper, count(robot, Items.IRON_INGOT) == 62 && count(chest, Items.IRON_INGOT) == 5,
        "A refused stack must stay in the chest: robot " + count(robot, Items.IRON_INGOT) + ", chest " + count(chest, Items.IRON_INGOT));
      chest.setItem(0, new ItemStack(Items.IRON_INGOT, 2));
      chest.setItem(1, new ItemStack(Items.IRON_INGOT, 3));
      check(helper, AIRobotLoad.load(robot, station, stack -> true, AIRobotLoad.ANY_QUANTITY, true), "Two ingots fit onto 62");
      check(helper, count(robot, Items.IRON_INGOT) == 64 && count(chest, Items.IRON_INGOT) == 3,
        "Only what fits may be taken: robot " + count(robot, Items.IRON_INGOT) + ", chest " + count(chest, Items.IRON_INGOT));

      for (int i = 0; i < robot.getContainerSize(); i++) {
        robot.setItem(i, ItemStack.EMPTY);
      }
      check(helper, AIRobotLoad.load(robot, station, stack -> true, 2, true), "Two of three ingots can be loaded");
      check(helper, count(robot, Items.IRON_INGOT) == 2 && count(chest, Items.IRON_INGOT) == 1,
        "Exactly the asked amount must be taken: robot " + count(robot, Items.IRON_INGOT) + ", chest " + count(chest, Items.IRON_INGOT));
      check(helper, !AIRobotLoad.load(robot, station, stack -> true, 5, true), "Asking for more than there is must not succeed");
      check(helper, count(robot, Items.IRON_INGOT) == 3 && count(chest, Items.IRON_INGOT) == 0,
        "What there is must be taken once: robot " + count(robot, Items.IRON_INGOT) + ", chest " + count(chest, Items.IRON_INGOT));
      helper.succeed();
    });
  }

  /** Original AIRobotLoadFluids.load(): a robot's tank takes at most 4000 mB of one fluid. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void fluidLoadingStopsAtTankCapacityAndFluid(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos.south(), FactoryBlocks.TANK.get().defaultBlockState());
    TankBlockEntity tank = (TankBlockEntity) helper.getBlockEntity(pos.south());
    tank.tank.fill(new FluidStack(Fluids.WATER, 8000), IFluidHandler.FluidAction.EXECUTE);
    DockingStation<?> station = placeStation(helper, pos, PipeType.FLUID, PipeMaterial.WOOD, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationProvideFluids);
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    pipe.setExtractionSide(Direction.SOUTH);

    helper.runAfterDelay(20, () -> {
      int before = tank.tank.getFluidAmount() + pipeFluid(pipe);
      RobotEntity robot = looseRobot(helper);
      robot.fill(new FluidStack(Fluids.WATER, 3999), IFluidHandler.FluidAction.EXECUTE);
      int loaded = AIRobotLoadFluids.load(robot, station, fluid -> true, true);
      int after = tank.tank.getFluidAmount() + pipeFluid(pipe);
      check(helper, loaded == 1 && robot.getFluidInTank(0).getAmount() == 4000,
        "A robot with 3999 mB must take exactly 1 mB, took " + loaded + ", holds " + robot.getFluidInTank(0).getAmount());
      check(helper, before - after == 1, "The station must give exactly what the robot took, gave " + (before - after));
      check(helper, AIRobotLoadFluids.load(robot, station, fluid -> true, true) == 0, "A full robot must not load");

      robot.drain(4000, IFluidHandler.FluidAction.EXECUTE);
      robot.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
      int beforeLava = tank.tank.getFluidAmount() + pipeFluid(pipe);
      check(helper, AIRobotLoadFluids.load(robot, station, fluid -> true, true) == 0,
        "A robot holding lava must not take water");
      check(helper, robot.getFluidInTank(0).getFluid() == Fluids.LAVA && robot.getFluidInTank(0).getAmount() == 1000
          && tank.tank.getFluidAmount() + pipeFluid(pipe) == beforeLava,
        "Nothing may be mixed or lost, robot holds " + robot.getFluidInTank(0).getAmount() + " of " + robot.getFluidInTank(0).getFluid());
      helper.succeed();
    });
  }

  /**
   * Original AIRobotUnloadFluids.unload(): a robot only hands over what the station's pipe takes. With the tank
   * behind it holding another fluid, the water stays in the robot and the pipe, and nothing is mixed.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void fluidUnloadingKeepsWhatTheTargetRefuses(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos.south(), FactoryBlocks.TANK.get().defaultBlockState());
    TankBlockEntity tank = (TankBlockEntity) helper.getBlockEntity(pos.south());
    tank.tank.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
    DockingStation<?> station = placeStation(helper, pos, PipeType.FLUID, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationAcceptFluids);
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    RobotEntity robot = looseRobot(helper);
    robot.fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);

    helper.onEachTick(() -> {
      if (helper.getTick() >= 20) {
        AIRobotUnloadFluids.unload(robot, station, true);
      }
      int water = robot.getFluidInTank(0).getAmount() + pipeFluid(pipe);
      check(helper, water == 4000, "Unloading must not lose or create water, robot " + robot.getFluidInTank(0).getAmount()
        + " pipe " + pipeFluid(pipe) + " at tick " + helper.getTick());
      check(helper, tank.tank.getFluid().getFluid() == Fluids.LAVA && tank.tank.getFluidAmount() == 1000,
        "The lava tank must not take water, holds " + tank.tank.getFluidAmount() + " of " + tank.tank.getFluid().getFluid());
    });
    helper.runAtTickTime(120, () -> {
      check(helper, robot.getFluidInTank(0).getAmount() < 4000, "The pipe itself takes some water");
      helper.succeed();
    });
  }

  /** A target tank with little room takes only that much; the rest stays in the robot or the pipe. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 300)
  public void fluidUnloadingFillsOnlyTheRoomLeft(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos.south(), FactoryBlocks.TANK.get().defaultBlockState());
    TankBlockEntity tank = (TankBlockEntity) helper.getBlockEntity(pos.south());
    int room = 1500;
    tank.tank.fill(new FluidStack(Fluids.WATER, tank.tank.getCapacity() - room), IFluidHandler.FluidAction.EXECUTE);
    int total = tank.tank.getFluidAmount() + 4000;
    DockingStation<?> station = placeStation(helper, pos, PipeType.FLUID, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationAcceptFluids);
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    RobotEntity robot = looseRobot(helper);
    robot.fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);

    helper.onEachTick(() -> {
      if (helper.getTick() >= 20) {
        AIRobotUnloadFluids.unload(robot, station, true);
      }
      int sum = robot.getFluidInTank(0).getAmount() + pipeFluid(pipe) + tank.tank.getFluidAmount();
      check(helper, sum == total, "Unloading must not lose or create water, total " + sum + " instead of " + total);
      check(helper, tank.tank.getFluidAmount() <= tank.tank.getCapacity(), "The tank must not overflow");
    });
    helper.succeedWhen(() -> {
      helper.assertTrue(tank.tank.getFluidAmount() == tank.tank.getCapacity(), "The tank must fill its remaining room, has "
        + tank.tank.getFluidAmount());
      helper.assertTrue(robot.getFluidInTank(0).getAmount() + pipeFluid(pipe) == 4000 - room,
        "What does not fit stays in the robot and the pipe");
    });
  }

  // ---------------------------------------------------------------- broken station, partial request

  /**
   * The requesting station is broken while a delivery robot has reserved its request: the station leaves the
   * registry, and once a new station is placed the request is delivered exactly once.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void brokenRequestingStationIsDeliveredOnceRebuilt(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(HOME.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
    helper.setBlock(JOB.south(), RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(JOB.south());
    requester.setRequest(0, new ItemStack(Items.IRON_INGOT, 3));

    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationProvideItems);
    DockingStation<?> target = placeStation(helper, JOB, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    ResourceIdRequest request = new ResourceIdRequest(target, 0);
    PipeBlockEntity targetPipe = (PipeBlockEntity) helper.getBlockEntity(JOB);

    RobotEntity[] holder = new RobotEntity[1];
    int[] brokenAt = {-1};
    helper.runAfterDelay(20, () -> holder[0] = spawnRobot(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), RobotEntityBase.MAX_ENERGY));
    helper.onEachTick(() -> {
      RobotEntity robot = holder[0];
      if (robot == null) return;
      int total = count(chest, Items.IRON_INGOT) + count(requester, Items.IRON_INGOT) + count(robot, Items.IRON_INGOT)
        + dropped(helper, Items.IRON_INGOT);
      check(helper, total == 8, "No ingot may be lost or created, counted " + total);
      if (brokenAt[0] < 0 && registry.robotIdTaking(request) == robot.getRobotId()) {
        targetPipe.setPipePluggable(Direction.UP, null);
        check(helper, registry.getStation(target.pos(), target.side()) == null, "A broken station must leave the registry");
        brokenAt[0] = (int) helper.getTick();
      }
      if (brokenAt[0] >= 0 && helper.getTick() == brokenAt[0] + 40) {
        targetPipe.setPipePluggable(Direction.UP, new RobotStationPluggable());
        check(helper, registry.getStation(target.pos(), target.side()) != null, "The rebuilt station must be registered");
      }
      if (helper.getTick() == TIMEOUT - 10) {
        robot.discard();
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity robot = holder[0];
      helper.assertTrue(robot != null && brokenAt[0] >= 0, "The station has not been broken yet");
      helper.assertTrue(requester.isFulfilled(0) && count(requester, Items.IRON_INGOT) == 3,
        "The request must be delivered once the station is rebuilt, requester has " + requester.getItem(0));
      helper.assertTrue(count(chest, Items.IRON_INGOT) == 5 && !robot.containsItems() && dropped(helper, Items.IRON_INGOT) == 0,
        "Exactly the requested amount must leave the chest, left " + chest.getItem(0) + ", dropped " + dropped(helper, Items.IRON_INGOT));
      robot.discard();
    });
  }

  /**
   * A requester that already holds one of three ingots asks for two. A robot that is reloaded with those two on
   * board delivers them once; the request is not delivered twice.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void partlyFulfilledRequestResumesAfterReload(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(HOME.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
    helper.setBlock(JOB.south(), RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(JOB.south());
    requester.setRequest(0, new ItemStack(Items.IRON_INGOT, 3));
    requester.setItem(0, new ItemStack(Items.IRON_INGOT, 1));
    check(helper, requester.getRequest(0).getCount() == 2, "A requester holding one of three asks for two, asks " + requester.getRequest(0));

    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionStationProvideItems);
    placeStation(helper, JOB, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);

    RobotEntity[] robots = new RobotEntity[2];
    helper.runAfterDelay(20, () -> robots[0] = spawnRobot(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), RobotEntityBase.MAX_ENERGY));
    helper.onEachTick(() -> {
      RobotEntity first = robots[0];
      if (first != null && robots[1] == null && first.containsItems()) {
        check(helper, count(first, Items.IRON_INGOT) == 2 && count(chest, Items.IRON_INGOT) == 6,
          "The robot loads only the missing two, carries " + count(first, Items.IRON_INGOT) + ", chest " + chest.getItem(0));
        CompoundTag saved = first.saveWithoutId(new CompoundTag());
        first.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        RobotEntity reloaded = new RobotEntity(RoboticsEntityTypes.ROBOT.get(), helper.getLevel());
        reloaded.load(saved);
        check(helper, helper.getLevel().addFreshEntity(reloaded), "The reloaded robot must spawn");
        robots[1] = reloaded;
      }
      if (helper.getTick() == TIMEOUT - 10) {
        for (RobotEntity robot : robots) {
          if (robot != null) robot.discard();
        }
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity reloaded = robots[1];
      helper.assertTrue(reloaded != null, "The robot has not loaded yet");
      helper.assertTrue(requester.isFulfilled(0) && count(requester, Items.IRON_INGOT) == 3,
        "The request must be completed once, requester has " + requester.getItem(0));
      helper.assertTrue(count(chest, Items.IRON_INGOT) == 6 && !reloaded.containsItems() && dropped(helper, Items.IRON_INGOT) == 0,
        "Only the missing two may leave the chest, left " + chest.getItem(0));
      reloaded.discard();
    });
  }

  // ---------------------------------------------------------------- power

  /**
   * An iron power pipe limited to 20 RF/t by a gate (PowerMode M2) feeds a robot station and a machine: together
   * they never get more than the limit.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void limitedPowerPipeCapsStationAndMachine(GameTestHelper helper) {
    BlockPos engine = new BlockPos(0, 2, 1);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
    helper.setBlock(new BlockPos(1, 2, 1), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    BlockPos machinePos = new BlockPos(2, 2, 2);
    helper.setBlock(machinePos, FactoryBlocks.REFINERY.get().defaultBlockState());
    RefineryBlockEntity machine = (RefineryBlockEntity) helper.getBlockEntity(machinePos);
    BlockPos ironPos = new BlockPos(2, 2, 1);
    DockingStation<?> station = placeStation(helper, ironPos, PipeType.ENERGY, PipeMaterial.IRON, Direction.UP, Direction.NORTH,
      TransportStatements.ACTION_POWER_LIMITER.get(PowerMode.M2).get());
    PipeBlockEntity iron = (PipeBlockEntity) helper.getBlockEntity(ironPos);
    int limit = PowerMode.M2.maxPower;
    int start = RobotEntityBase.SAFETY_ENERGY / 2;
    RobotEntity robot = spawnRobot(helper, station, RoboticsRedstoneRobots.ROBOT_LUMBERJACK.get(), start);

    int from = 100;
    int to = 300;
    int[] atStart = new int[1];
    helper.onEachTick(() -> {
      long tick = helper.getTick();
      if (tick == from) {
        check(helper, iron.getIronPipeEnergyLimit() == limit && iron.getEnergyTransportModule().getMaxPower() == limit,
          "The gate must limit the iron pipe to " + limit + " RF/t, limit " + iron.getIronPipeEnergyLimit());
        atStart[0] = robot.getEnergy() + machine.getEnergyStored();
      }
      if (tick == to) {
        int gained = robot.getEnergy() + machine.getEnergyStored() - atStart[0];
        // The pipe may hand over what it held in its buffer (at most its limit) on top of the limit per tick.
        int cap = limit * (to - from) + limit;
        try {
          check(helper, gained > 0, "The limited pipe must still deliver energy");
          check(helper, gained <= cap, "The station and the machine got " + gained + " RF in " + (to - from)
            + " ticks, more than the limit allows (" + cap + ")");
          check(helper, robot.getDockingStation() == station, "A robot that recharges stays on its power station");
          helper.succeed();
        } finally {
          robot.discard();
        }
      }
    });
  }

  // ---------------------------------------------------------------- boards without anything to do

  /** Original BoardRobotPump: a robot that still holds another fluid unloads first and does not pump. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void pumpHoldingOtherFluidLeavesSource(GameTestHelper helper) {
    basin(helper);
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);
    runIdleBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PUMP.get(), RobotEntityBase.MAX_ENERGY,
      robot -> robot.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE), 300, robot -> {
        check(helper, helper.getBlockState(JOB).is(Blocks.WATER), "The water source must stay, found " + helper.getBlockState(JOB));
        check(helper, robot.getFluidInTank(0).getFluid() == Fluids.LAVA && robot.getFluidInTank(0).getAmount() == 1000,
          "The robot must keep its lava and take no water, holds " + robot.getFluidInTank(0).getAmount() + " of " + robot.getFluidInTank(0).getFluid());
      });
  }

  /** Original BoardRobotPump.updateFilter(): a lava filter on the station leaves water sources alone. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void pumpFilterLeavesOtherFluids(GameTestHelper helper) {
    basin(helper);
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH,
      RoboticsStatements.actionRobotFilter);
    gate(helper, HOME, Direction.NORTH).setActionParameter(1, 0, new StatementParameterItemStack(new ItemStack(Items.LAVA_BUCKET)));
    runIdleBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PUMP.get(), RobotEntityBase.MAX_ENERGY, robot -> {
    }, 300, robot -> {
      check(helper, helper.getBlockState(JOB).is(Blocks.WATER), "A lava filter must leave the water source, found " + helper.getBlockState(JOB));
      check(helper, robot.getFluidInTank(0).isEmpty(), "The robot must not hold water, holds " + robot.getFluidInTank(0).getAmount());
    });
  }

  /** A single water source in a stone basin, open at the top. */
  private static void basin(GameTestHelper helper) {
    helper.setBlock(JOB.below(), Blocks.STONE.defaultBlockState());
    for (Direction side : Direction.Plane.HORIZONTAL) {
      helper.setBlock(JOB.relative(side), Blocks.STONE.defaultBlockState());
    }
    helper.setBlock(JOB, Blocks.WATER.defaultBlockState());
  }

  /** Original AIRobotPlant: seeds are only planted on a block that sustains them, and are kept otherwise. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void planterKeepsSeedsWithoutSoil(GameTestHelper helper) {
    helper.setBlock(JOB, Blocks.STONE.defaultBlockState());
    helper.setBlock(JOB.above().south(), Blocks.GLOWSTONE.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);
    runIdleBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PLANTER.get(), RobotEntityBase.MAX_ENERGY,
      robot -> robot.setItemInUse(new ItemStack(Items.WHEAT_SEEDS, 4)), 300, robot -> {
        check(helper, helper.getBlockState(JOB.above()).isAir(), "Nothing may be planted on stone, found " + helper.getBlockState(JOB.above()));
        int seeds = count(robot, Items.WHEAT_SEEDS) + (robot.getMainHandItem().is(Items.WHEAT_SEEDS) ? robot.getMainHandItem().getCount() : 0)
          + dropped(helper, Items.WHEAT_SEEDS);
        check(helper, seeds == 4, "No seed may be used up without planting, counted " + seeds);
      });
  }

  /** Original BoardRobotPlanter: a robot holding something that is not a seed plants nothing. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void planterPlacesNothingThatIsNotASeed(GameTestHelper helper) {
    helper.setBlock(JOB.below(), Blocks.STONE.defaultBlockState());
    helper.setBlock(JOB, Blocks.FARMLAND.defaultBlockState());
    helper.setBlock(JOB.above().south(), Blocks.GLOWSTONE.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);
    runIdleBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PLANTER.get(), RobotEntityBase.MAX_ENERGY,
      robot -> robot.setItemInUse(new ItemStack(Items.COBBLESTONE, 4)), 300, robot -> {
        check(helper, helper.getBlockState(JOB.above()).isAir(), "Cobblestone must not be planted, found " + helper.getBlockState(JOB.above()));
        check(helper, helper.getBlockState(JOB).is(Blocks.FARMLAND), "The farmland must stay, found " + helper.getBlockState(JOB));
      });
  }

  // ---------------------------------------------------------------- energy loss and recovery

  /** A robot that runs out of energy shuts down where it is and keeps what it carries. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = SMALL, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void shutDownRobotKeepsItsLoad(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    DockingStation<?> station = placeStation(helper, pos, PipeType.ITEM, PipeMaterial.COBBLESTONE, Direction.UP, Direction.NORTH);
    RobotEntity robot = spawnRobot(helper, station, RoboticsRedstoneRobots.ROBOT_CARRIER.get(), 0);
    robot.setItem(0, new ItemStack(Items.IRON_INGOT, 5));
    robot.fill(new FluidStack(Fluids.WATER, 2000), IFluidHandler.FluidAction.EXECUTE);
    helper.runAtTickTime(80, () -> {
      try {
        check(helper, robot.isAlive() && robot.mainAI.getActiveAI() instanceof AIRobotShutdown,
          "A robot without energy must shut down, runs " + robot.mainAI.getActiveAI());
        check(helper, count(robot, Items.IRON_INGOT) == 5 && robot.getFluidInTank(0).getAmount() == 2000,
          "A shut down robot keeps its load, has " + count(robot, Items.IRON_INGOT) + " ingots and " + robot.getFluidInTank(0).getAmount() + " mB");
        check(helper, dropped(helper, Items.IRON_INGOT) == 0, "Nothing may be dropped");
        helper.succeed();
      } finally {
        robot.discard();
      }
    });
  }

  /**
   * Original AIRobotMain.preempt(): a robot below its safety energy recharges on its power station first and then
   * does its job.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = BIG, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void lowRobotRechargesThenWorks(GameTestHelper helper) {
    helper.setBlock(JOB.below(), Blocks.STONE.defaultBlockState());
    helper.setBlock(JOB, Blocks.FARMLAND.defaultBlockState());
    helper.setBlock(JOB.above().south(), Blocks.GLOWSTONE.defaultBlockState());
    BlockPos engine = HOME.west(2);
    helper.setBlock(engine.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(engine, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
    helper.setBlock(HOME.west(), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ENERGY, PipeMaterial.GOLD, Direction.UP, Direction.NORTH);
    check(helper, home.providesPower(), "A station on a power pipe provides power");

    int start = RobotEntityBase.SAFETY_ENERGY / 2;
    RobotEntity[] holder = new RobotEntity[1];
    boolean[] recharged = new boolean[1];
    helper.runAfterDelay(20, () -> {
      holder[0] = spawnRobot(helper, home, RoboticsRedstoneRobots.ROBOT_PLANTER.get(), start);
      holder[0].setItemInUse(new ItemStack(Items.WHEAT_SEEDS));
    });
    helper.onEachTick(() -> {
      RobotEntity robot = holder[0];
      if (robot == null) return;
      if (!recharged[0]) {
        check(helper, !helper.getBlockState(JOB.above()).is(Blocks.WHEAT), "A robot below its safety energy must recharge before it works");
        recharged[0] = robot.getEnergy() >= RobotEntityBase.SAFETY_ENERGY;
      }
      if (helper.getTick() == TIMEOUT - 10) {
        robot.discard();
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity robot = holder[0];
      helper.assertTrue(robot != null && recharged[0], "The robot has not recharged yet");
      helper.assertTrue(helper.getBlockState(JOB.above()).is(Blocks.WHEAT), "After recharging the robot must plant, energy " + robot.getEnergy());
      robot.discard();
    });
  }
}
