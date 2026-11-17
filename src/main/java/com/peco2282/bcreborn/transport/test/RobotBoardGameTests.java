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
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRobotRegistry;
import com.peco2282.bcreborn.api.robots.ResourceIdRequest;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.robots.RobotManager;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.core.statements.DefaultTriggerProvider;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsEntityTypes;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.robotics.statements.ActionRobotWorkInArea;
import com.peco2282.bcreborn.robotics.statements.RoboticsStatements;
import com.peco2282.bcreborn.robotics.statements.StatementParameterMapLocation;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GateFactory;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import java.util.List;
import java.util.function.Supplier;
import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BptContext;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Runs the robot boards that work on blocks (original {@code BoardRobotMiner}, {@code BoardRobotLumberjack},
 * {@code BoardRobotHarvester}, {@code BoardRobotFarmer}, {@code BoardRobotPlanter}, {@code BoardRobotPump}) from
 * their station to the finished job.
 * <p>
 * Every robot gets a work area (original {@code ActionRobotWorkInArea} with a map location) that covers only its
 * own test structure: a robot without one searches 64 blocks around itself and would work on other tests.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class RobotBoardGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos HOME = new BlockPos(3, 3, 3);
  /** The block the robot works on; it stands on a stone block of its own. */
  private static final BlockPos JOB = new BlockPos(8, 3, 3);
  private static final int TIMEOUT = 1500;

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /** A map location item for the inside of this test's structure. */
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
   * A robot station on a pipe at {@code pos}, with a gate that keeps the robot inside the test structure and
   * activates {@code actions} as well.
   */
  private static DockingStation<?> placeStation(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material,
                                                IStatement... actions) {
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

    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.EMERALD, GateDefinition.GateLogic.AND, Direction.NORTH);
    pipe.setPipePluggable(Direction.NORTH, new GatePluggable(gate));
    gate.setTrigger(0, DefaultTriggerProvider.triggerRedstoneInactive);
    gate.setAction(0, RoboticsStatements.actionRobotWorkInArea);
    gate.setActionParameter(0, 0, new StatementParameterItemStack(areaMap(helper)));
    for (int i = 0; i < actions.length; i++) {
      gate.setTrigger(i + 1, DefaultTriggerProvider.triggerRedstoneInactive);
      gate.setAction(i + 1, actions[i]);
    }
    return station;
  }

  private static RobotEntity placeRobot(GameTestHelper helper, DockingStation<?> station, RedstoneBoardRobotNBT board, ItemStack tool) {
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    RobotEntity robot = new RobotEntity(helper.getLevel(), board);
    robot.setUniqueRobotId(registry.getNextRobotId());
    robot.setPos(station.x() + 0.5, station.y() + 0.5, station.z() + 0.5);
    robot.getBattery().receiveEnergy(RobotEntityBase.MAX_ENERGY, false);
    if (!tool.isEmpty()) {
      robot.setItemInUse(tool);
    }
    check(helper, helper.getLevel().addFreshEntity(robot), "The robot must spawn");
    check(helper, station.takeAsMain(robot), "A free station must accept the robot as its main robot");
    robot.dock(station);
    return robot;
  }

  private static String describe(RobotEntity robot) {
    return "robot at " + robot.blockPosition() + " holding " + robot.getMainHandItem() + " running "
      + robot.mainAI.getActiveAI() + " energy " + robot.getEnergy() + " zone " + robot.getZoneToWork();
  }

  /**
   * Places the robot once the station's gate has resolved its actions, discards it when the test ends either
   * way, and succeeds when {@code done} holds.
   */
  private static void runBoard(GameTestHelper helper, DockingStation<?> home, RedstoneBoardRobotNBT board, ItemStack tool,
                               java.util.function.Consumer<RobotEntity> done) {
    RobotEntity[] holder = new RobotEntity[1];
    helper.runAfterDelay(20, () -> holder[0] = placeRobot(helper, home, board, tool));
    helper.onEachTick(() -> {
      if (helper.getTick() == TIMEOUT - 10 && holder[0] != null) {
        holder[0].discard();
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity robot = holder[0];
      helper.assertTrue(robot != null, "The robot is not placed yet");
      IZone zone = robot.getZoneToWork();
      helper.assertTrue(zone != null && zone.contains(robot.getX(), robot.getY(), robot.getZ()),
        "The robot must have its work area and stay inside it, " + describe(robot));
      done.accept(robot);
      robot.discard();
    });
  }

  private static void assertThat(GameTestHelper helper, RobotEntity robot, boolean condition, Supplier<String> message) {
    helper.assertTrue(condition, message.get() + ", " + describe(robot));
  }

  /**
   * A one-block pen, two blocks high and open at the top, so a hit cannot knock the mob out of the work area.
   */
  private static void pen(GameTestHelper helper, BlockPos pos) {
    stand(helper, pos);
    for (Direction side : Direction.Plane.HORIZONTAL) {
      helper.setBlock(pos.relative(side), Blocks.STONE.defaultBlockState());
      helper.setBlock(pos.relative(side).above(), Blocks.STONE.defaultBlockState());
    }
  }

  private static void stand(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
  }

  /** Original BoardRobotMiner: fetches a pickaxe from its station, then mines the ores its pickaxe can harvest. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void minerFetchesPickaxeAndMinesOre(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    ((Container) helper.getBlockEntity(HOME.south())).setItem(0, new ItemStack(Items.IRON_PICKAXE));
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.IRON_ORE.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, RoboticsStatements.actionStationProvideItems);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_MINER.get(), ItemStack.EMPTY, robot -> {
      assertThat(helper, robot, robot.getMainHandItem().is(Items.IRON_PICKAXE), () -> "The miner must equip the pickaxe from its station");
      assertThat(helper, robot, helper.getBlockState(JOB).isAir(), () -> "The miner must mine the iron ore");
      helper.assertItemEntityPresent(Items.RAW_IRON, JOB, 3);
      assertThat(helper, robot, robot.getMainHandItem().getDamageValue() > 0, () -> "Mining must wear the pickaxe");
    });
  }

  /** Original BoardRobotLumberjack: breaks wood with an axe. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void lumberjackCutsLog(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.OAK_LOG.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_LUMBERJACK.get(), new ItemStack(Items.IRON_AXE), robot -> {
      assertThat(helper, robot, helper.getBlockState(JOB).isAir(), () -> "The lumberjack must cut the log");
      helper.assertItemEntityPresent(Items.OAK_LOG, JOB, 3);
    });
  }

  /** Original BoardRobotHarvester / AIRobotHarvest: harvests mature crops and drops the produce at the robot. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void harvesterHarvestsMatureCrop(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.FARMLAND.defaultBlockState());
    helper.setBlock(JOB.above(), Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, CropBlock.MAX_AGE));
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_HARVESTER.get(), ItemStack.EMPTY, robot -> {
      assertThat(helper, robot, helper.getBlockState(JOB.above()).isAir(), () -> "The harvester must harvest the wheat");
      helper.assertItemEntityPresent(Items.WHEAT, JOB.above(), 4);
    });
  }

  /** Original BoardRobotFarmer / AIRobotUseToolOnBlock: tills dirt with a hoe (it must not break the dirt). */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void farmerTillsDirt(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.DIRT.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_FARMER.get(), new ItemStack(Items.IRON_HOE), robot ->
      assertThat(helper, robot, helper.getBlockState(JOB).is(Blocks.FARMLAND), () -> "The farmer must turn the dirt into farmland, found " + helper.getBlockState(JOB)));
  }

  /** Original BoardRobotPlanter / AIRobotPlant: plants the seed it holds on a block that can sustain it. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void planterPlantsSeed(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.FARMLAND.defaultBlockState());
    // The test structure is dark and closed to the sky; wheat can only be planted in light.
    helper.setBlock(JOB.above().south(), Blocks.GLOWSTONE.defaultBlockState());
    BlockPos abs = helper.absolutePos(JOB);
    check(helper, CropManager.canSustainPlant(helper.getLevel(), new ItemStack(Items.WHEAT_SEEDS), abs), "The farmland must sustain wheat seeds");
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PLANTER.get(), new ItemStack(Items.WHEAT_SEEDS), robot ->
      assertThat(helper, robot, helper.getBlockState(JOB.above()).is(Blocks.WHEAT), () -> "The planter must plant the seed on the farmland, found " + helper.getBlockState(JOB.above())));
  }

  /** Original BoardRobotPump / AIRobotPumpBlock: takes a fluid source block into the robot's tank. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void pumpTakesFluidSource(GameTestHelper helper) {
    // A basin so the water stays a single source block; it is open at the top.
    stand(helper, JOB);
    for (Direction side : Direction.Plane.HORIZONTAL) {
      helper.setBlock(JOB.relative(side), Blocks.STONE.defaultBlockState());
    }
    helper.setBlock(JOB, Blocks.WATER.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PUMP.get(), ItemStack.EMPTY, robot -> {
      assertThat(helper, robot, robot.getFluidInTank(0).getFluid() == Fluids.WATER && robot.getFluidInTank(0).getAmount() == 1000,
        () -> "The pump must hold one bucket of water, holds " + robot.getFluidInTank(0).getAmount());
      assertThat(helper, robot, helper.getBlockState(JOB).isAir(), () -> "The pumped source block must be gone, found " + helper.getBlockState(JOB));
    });
  }

  /** Original BoardRobotShovelman: digs what a shovel digs. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void shovelmanDigsClay(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.CLAY.defaultBlockState());
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_SHOVELMAN.get(), new ItemStack(Items.IRON_SHOVEL), robot -> {
      assertThat(helper, robot, helper.getBlockState(JOB).isAir(), () -> "The shovelman must dig the clay");
      helper.assertItemEntityPresent(Items.CLAY_BALL, JOB, 3);
    });
  }

  /** Original BoardRobotLeaveCutter: cuts leaves with shears, which gives the leaves themselves. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void leaveCutterShearsLeaves(GameTestHelper helper) {
    stand(helper, JOB);
    helper.setBlock(JOB, Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_LEAVE_CUTTER.get(), new ItemStack(Items.SHEARS), robot -> {
      assertThat(helper, robot, helper.getBlockState(JOB).isAir(), () -> "The leave cutter must cut the leaves");
      helper.assertItemEntityPresent(Items.OAK_LEAVES, JOB, 3);
    });
  }

  /** Original BoardRobotPicker / AIRobotFetchItem: picks up items lying in its work area. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void pickerCollectsDroppedItems(GameTestHelper helper) {
    stand(helper, JOB);
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    helper.spawnItem(Items.DIAMOND, JOB.getX() + 0.5f, JOB.getY() + 0.2f, JOB.getZ() + 0.5f);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_PICKER.get(), ItemStack.EMPTY, robot -> {
      boolean holdsDiamond = false;
      for (int i = 0; i < robot.getContainerSize(); i++) {
        holdsDiamond |= robot.getItem(i).is(Items.DIAMOND);
      }
      boolean found = holdsDiamond;
      assertThat(helper, robot, found, () -> "The picker must carry the diamond it picked up");
      helper.assertItemEntityNotPresent(Items.DIAMOND, JOB, 4);
    });
  }

  /**
   * Original BoardRobotDelivery: finds a station with an open item request (a Requester next to its pipe), loads
   * the requested amount at a station that provides it, and hands it to the requester.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void deliveryBringsRequestedItems(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(HOME.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
    helper.setBlock(JOB.south(), RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(JOB.south());
    requester.setRequest(0, new ItemStack(Items.IRON_INGOT, 3));
    check(helper, !requester.isFulfilled(0) && requester.getRequest(0).getCount() == 3, "The requester must ask for three ingots");

    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, RoboticsStatements.actionStationProvideItems);
    DockingStation<?> target = placeStation(helper, JOB, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    check(helper, target.getRequestProvider() == requester, "A station next to a requester offers its requests");

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), ItemStack.EMPTY, robot -> {
      assertThat(helper, robot, requester.getItem(0).is(Items.IRON_INGOT) && requester.getItem(0).getCount() == 3,
        () -> "The requester must receive three ingots, has " + requester.getItem(0));
      assertThat(helper, robot, requester.isFulfilled(0), () -> "The request must be fulfilled");
      assertThat(helper, robot, chest.getItem(0).getCount() == 5, () -> "Only the requested amount may leave the chest, left " + chest.getItem(0));
    });
  }

  /**
   * Original RobotRegistry.killRobot(): a delivery robot that dies while it has reserved a request and the
   * requesting station gives up both, and its main station too, so that another robot can deliver the request.
   * The first robot is killed before it has loaded anything, so the ingots in the chest stay accounted for.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void deadDeliveryRobotReleasesItsReservations(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(HOME.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
    helper.setBlock(JOB.south(), RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(JOB.south());
    requester.setRequest(0, new ItemStack(Items.IRON_INGOT, 3));

    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, RoboticsStatements.actionStationProvideItems);
    DockingStation<?> target = placeStation(helper, JOB, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    IRobotRegistry registry = RobotManager.registry().getRegistry(helper.getLevel());
    ResourceIdRequest request = new ResourceIdRequest(target, 0);

    RobotEntity[] robots = new RobotEntity[2];
    boolean[] killed = new boolean[1];
    helper.runAfterDelay(20, () -> robots[0] = placeRobot(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), ItemStack.EMPTY));
    helper.onEachTick(() -> {
      RobotEntity first = robots[0];
      if (first != null && !killed[0] && first.isAlive() && registry.robotIdTaking(request) == first.getRobotId()) {
        check(helper, target.robotIdTaking() == first.getRobotId(), "The delivery robot takes the requesting station with the request");
        check(helper, !first.containsItems(), "The robot must not have loaded anything when it reserves the request");
        // Like /kill: the robot plays the death animation and is removed (and unregistered) 20 ticks later.
        first.kill();
        killed[0] = true;
      }
      if (killed[0] && robots[1] == null && first.isRemoved()) {
        check(helper, !registry.isTaken(request), "A dead robot must release the request it reserved");
        check(helper, !target.isTaken(), "A dead robot must release the requesting station, taken by " + target.robotIdTaking());
        check(helper, !home.isTaken(), "A dead robot must release its main station, taken by " + home.robotIdTaking());
        check(helper, chest.getItem(0).getCount() == 8, "Nothing may leave the chest before loading, left " + chest.getItem(0));
        robots[1] = placeRobot(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), ItemStack.EMPTY);
      }
      if (helper.getTick() == TIMEOUT - 10) {
        for (RobotEntity robot : robots) {
          if (robot != null) robot.discard();
        }
      }
    });
    helper.succeedWhen(() -> {
      RobotEntity second = robots[1];
      helper.assertTrue(second != null, "The first robot has not reserved the request yet");
      assertThat(helper, second, requester.getItem(0).is(Items.IRON_INGOT) && requester.getItem(0).getCount() == 3,
        () -> "The second robot must deliver the released request, requester has " + requester.getItem(0));
      assertThat(helper, second, chest.getItem(0).getCount() == 5 && !second.containsItems(),
        () -> "Exactly the requested amount must leave the chest, left " + chest.getItem(0));
      second.discard();
    });
  }

  /**
   * A delivery robot that is unloaded with the requested items on board and loaded again (as when its chunk
   * unloads) keeps its request and its load, and delivers the requested amount once: nothing is lost or delivered
   * twice.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void deliveryResumesAfterReload(GameTestHelper helper) {
    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    Container chest = (Container) helper.getBlockEntity(HOME.south());
    chest.setItem(0, new ItemStack(Items.IRON_INGOT, 8));
    helper.setBlock(JOB.south(), RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(JOB.south());
    requester.setRequest(0, new ItemStack(Items.IRON_INGOT, 3));

    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, RoboticsStatements.actionStationProvideItems);
    placeStation(helper, JOB, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    RobotEntity[] robots = new RobotEntity[2];
    helper.runAfterDelay(20, () -> robots[0] = placeRobot(helper, home, RoboticsRedstoneRobots.ROBOT_DELIVERY.get(), ItemStack.EMPTY));
    helper.onEachTick(() -> {
      RobotEntity first = robots[0];
      if (first != null && robots[1] == null && first.containsItems()) {
        int carried = carried(first, Items.IRON_INGOT);
        check(helper, carried == 3 && chest.getItem(0).getCount() == 5,
          "The robot loads exactly the requested amount, carries " + carried + ", chest " + chest.getItem(0));
        CompoundTag saved = first.saveWithoutId(new CompoundTag());
        first.remove(net.minecraft.world.entity.Entity.RemovalReason.UNLOADED_TO_CHUNK);
        RobotEntity reloaded = new RobotEntity(RoboticsEntityTypes.ROBOT.get(), helper.getLevel());
        reloaded.load(saved);
        check(helper, helper.getLevel().addFreshEntity(reloaded), "The reloaded robot must spawn");
        check(helper, carried(reloaded, Items.IRON_INGOT) == 3, "The reloaded robot keeps its load");
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
      helper.assertTrue(reloaded != null, "The robot has not loaded the request yet");
      assertThat(helper, reloaded, requester.isFulfilled(0) && requester.getItem(0).getCount() == 3,
        () -> "The reloaded robot must deliver the request once, requester has " + requester.getItem(0));
      assertThat(helper, reloaded, chest.getItem(0).getCount() == 5 && !reloaded.containsItems(),
        () -> "Exactly the requested amount must leave the chest, left " + chest.getItem(0) + ", robot still carries "
          + carried(reloaded, Items.IRON_INGOT));
      reloaded.discard();
    });
  }

  private static int carried(RobotEntity robot, net.minecraft.world.item.Item item) {
    int count = 0;
    for (int i = 0; i < robot.getContainerSize(); i++) {
      if (robot.getItem(i).is(item)) {
        count += robot.getItem(i).getCount();
      }
    }
    return count;
  }

  /** Original BoardRobotButcher / AIRobotAttack: kills animals in its work area with a sword. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void butcherKillsAnimal(GameTestHelper helper) {
    pen(helper, JOB);
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, JOB);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_BUTCHER.get(), new ItemStack(Items.IRON_SWORD), robot ->
      assertThat(helper, robot, !pig.isAlive(), () -> "The butcher must kill the pig, health " + pig.getHealth() + " at " + pig.blockPosition()));
  }

  /** Original BoardRobotKnight: attacks monsters, and leaves animals alone. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void knightKillsMonsterOnly(GameTestHelper helper) {
    pen(helper, JOB);
    BlockPos pen = JOB.offset(0, 0, 4);
    pen(helper, pen);
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, JOB);
    Pig pig = helper.spawnWithNoFreeWill(EntityType.PIG, pen);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_KNIGHT.get(), new ItemStack(Items.IRON_SWORD), robot -> {
      assertThat(helper, robot, !zombie.isAlive(), () -> "The knight must kill the zombie, health " + zombie.getHealth());
      assertThat(helper, robot, pig.isAlive() && pig.getHealth() == pig.getMaxHealth(), () -> "The knight must not touch the pig");
      pig.discard();
    });
  }

  /** Original BoardRobotStripes / AIRobotStripesHandler: uses the item it holds like a stripes pipe (a block is placed). */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void stripesRobotPlacesBlock(GameTestHelper helper) {
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_STRIPES.get(), new ItemStack(Items.LAPIS_BLOCK), robot -> {
      int placed = 0;
      for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(0, 1, 0), new BlockPos(12, 7, 12))) {
        if (helper.getBlockState(pos).is(Blocks.LAPIS_BLOCK)) placed++;
      }
      int count = placed;
      assertThat(helper, robot, count == 1, () -> "The stripes robot must place its block inside the work area, found " + count);
      assertThat(helper, robot, robot.getMainHandItem().isEmpty(), () -> "The placed block must leave the robot's hand");
    });
  }

  /**
   * Original BoardRobotBomber, the drop only: at the end of its flight the robot takes one TNT from its inventory
   * and primes it below itself. The flight is not run here: the explosion would damage the other tests.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void bomberDropsPrimedTnt(GameTestHelper helper) {
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    RobotEntity robot = placeRobot(helper, home, RoboticsRedstoneRobots.ROBOT_BOMBER.get(), ItemStack.EMPTY);
    robot.setItem(0, new ItemStack(Items.TNT, 2));
    robot.getBoard().delegateAIEnded(new AIRobotGotoBlock(robot, 0, 0, 0));

    List<PrimedTnt> primed = helper.getLevel().getEntitiesOfClass(PrimedTnt.class, robot.getBoundingBox().inflate(3));
    primed.forEach(PrimedTnt::discard);
    int left = robot.getItem(0).getCount();
    robot.discard();
    check(helper, primed.size() == 1, "The bomber must prime exactly one TNT, found " + primed.size());
    check(helper, left == 1, "Priming must use one TNT from the robot, left " + left);
    helper.succeed();
  }

  /**
   * Original BoardRobotBuilder: finds a construction marker with a blueprint in its work area, fetches what the
   * next block needs from a providing station (outside creative), flies near it and builds it.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = TIMEOUT)
  public void builderRobotBuildsBlueprintAtConstructionMarker(GameTestHelper helper) {
    BlockPos source = new BlockPos(10, 3, 10);
    BlockPos markerPos = new BlockPos(6, 3, 7);
    BlockPos target = markerPos.offset(2, 0, 2);
    helper.setBlock(source, Blocks.STONE_BRICKS.defaultBlockState());
    helper.setBlock(source.east(), Blocks.STONE_BRICKS.defaultBlockState());
    // The marker hangs on a block behind it (it faces east, so the blueprint is not rotated).
    helper.setBlock(markerPos.west(), Blocks.STONE.defaultBlockState());
    helper.setBlock(markerPos, BuildersBlock.CONSTRUCTION_MARKER.get().defaultBlockState()
      .setValue(BlockStateProperties.FACING, Direction.EAST));
    ConstructionMarkerBlockEntity marker = (ConstructionMarkerBlockEntity) helper.getBlockEntity(markerPos);

    // Record source and its eastern neighbour (as the Architect does); the copy goes two blocks east and south.
    Blueprint bpt = new Blueprint(2, 1, 1);
    BlockPos min = helper.absolutePos(source);
    BptContext context = bpt.getContext(helper.getLevel(), new Box(min, min.east()));
    bpt.readFromWorld(context, marker, min.getX(), min.getY(), min.getZ());
    bpt.readFromWorld(context, marker, min.getX() + 1, min.getY(), min.getZ());
    bpt.anchorX = -2;
    bpt.anchorY = 0;
    bpt.anchorZ = -2;
    bpt.id.name = "gametest_robot_builder";
    bpt.author = "GameTest";
    BCRebornBuilders.getServerDB().add(bpt.id, bpt.getNBT());
    marker.setBlueprint(bpt.getStack());

    helper.setBlock(HOME.south(), Blocks.CHEST.defaultBlockState());
    ((Container) helper.getBlockEntity(HOME.south())).setItem(0, new ItemStack(Items.STONE_BRICKS, 8));
    DockingStation<?> home = placeStation(helper, HOME, PipeType.ITEM, PipeMaterial.WOOD, RoboticsStatements.actionStationProvideItems);

    runBoard(helper, home, RoboticsRedstoneRobots.ROBOT_BUILDER.get(), ItemStack.EMPTY, robot -> {
      assertThat(helper, robot, marker.bluePrintBuilder != null, () -> "The marker must have loaded its blueprint");
      assertThat(helper, robot, helper.getBlockState(target).is(Blocks.STONE_BRICKS) && helper.getBlockState(target.east()).is(Blocks.STONE_BRICKS),
        () -> "The builder robot must build both blocks, found " + helper.getBlockState(target) + " and " + helper.getBlockState(target.east())
          + ", marker needs to build " + marker.needsToBuild());
    });
  }

  /**
   * A gate's statement parameters must come back from a save as separate instances with their contents: the work
   * area of a robot is such a parameter (a map location item).
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateParametersSurviveSaving(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 2, 1);
    helper.setBlock(pos, TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.EMERALD, GateDefinition.GateLogic.AND, Direction.NORTH);
    ItemStack map = new ItemStack(CoreItems.MAP_LOCATION.get());
    map.getOrCreateTag().putByte(MapLocationItem.TAG_KIND, MapLocationItem.KIND_AREA);
    map.getOrCreateTag().putInt("xMax", 5);
    StatementParameterMapLocation area = new StatementParameterMapLocation();
    area.onClick(gate, RoboticsStatements.actionRobotWorkInArea, map, null);
    gate.setAction(0, RoboticsStatements.actionRobotWorkInArea);
    gate.setActionParameter(0, 0, area);
    gate.setAction(1, RoboticsStatements.actionRobotLoadUnloadArea);
    gate.setActionParameter(1, 0, new StatementParameterItemStack(new ItemStack(Items.DIAMOND)));

    CompoundTag saved = new CompoundTag();
    gate.writeToNBT(saved);
    Gate loaded = GateFactory.makeGate(pipe.getPipe(), saved).orElse(null);
    check(helper, loaded != null, "A gate with parameters must load again");
    IStatementParameter first = loaded.getActionParameter(0, 0);
    IStatementParameter second = loaded.getActionParameter(1, 0);
    check(helper, first instanceof StatementParameterMapLocation && first != area, "The parameter must be read into a new instance of its own kind");
    check(helper, first.getItemStack().is(CoreItems.MAP_LOCATION.get()) && first.getItemStack().getOrCreateTag().getInt("xMax") == 5,
      "The map location parameter must keep its item, got " + first.getItemStack());
    check(helper, second != null && second != first && second.getItemStack().is(Items.DIAMOND),
      "A second parameter must not share the first one's instance, got " + (second == null ? null : second.getItemStack()));
    check(helper, loaded.getAction(0) instanceof ActionRobotWorkInArea, "The action must be read back");
    helper.succeed();
  }
}
