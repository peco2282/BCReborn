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

import com.mojang.authlib.GameProfile;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.core.statements.DefaultActionProvider;
import com.peco2282.bcreborn.core.statements.StatementParameterRedstoneGateSideOnly;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.menu.GateInterfaceMenu;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.extraction.FluidExtractionBattery;
import com.peco2282.bcreborn.transport.statements.ActionSignalOutput;
import io.netty.buffer.Unpooled;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The gate GUI rebuilt after the original {@code ContainerGateInterface} / {@code GuiGateInterface}, and the
 * wooden / emerald fluid pipes after the original {@code PipeFluidsWood} / {@code PipeFluidsEmerald}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class GateGuiFluidGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 2, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static Gate attachGate(PipeBlockEntity pipe, GateDefinition.GateMaterial material) {
    Gate gate = new Gate(pipe.getPipe(), material, GateDefinition.GateLogic.AND, Direction.UP);
    pipe.setPipePluggable(Direction.UP, new GatePluggable(gate));
    return gate;
  }

  private static FakePlayer player(GameTestHelper helper) {
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "gate"));
    BlockPos abs = helper.absolutePos(PIPE);
    player.setPos(abs.getX() + 0.5, abs.getY() + 1, abs.getZ() + 0.5);
    return player;
  }

  /**
   * Original Gate.addActions(): a wire's signal action is offered when the pipe has the wire and the gate's
   * material carries that colour (a redstone gate only red, an iron gate red and blue).
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateCandidatesFollowWiresAndMaterial(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    pipe.addWire(PipeWire.RED);
    pipe.addWire(PipeWire.BLUE);
    FakePlayer player = player(helper);

    attachGate(pipe, GateDefinition.GateMaterial.REDSTONE);
    List<IStatement> basic = new GateInterfaceMenu(1, player.getInventory(), pipe, Direction.UP).getPotentialActions();
    check(helper, basic.contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.RED).get())
        && !basic.contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.BLUE).get()),
      "A redstone gate offers the red signal only, got " + basic.stream().filter(s -> s instanceof ActionSignalOutput).toList());
    check(helper, basic.stream().noneMatch(s -> s.minParameters() > 0),
      "A gate without parameter slots must not offer statements that need a parameter");

    pipe.setPipePluggable(Direction.UP, null);
    attachGate(pipe, GateDefinition.GateMaterial.IRON);
    GateInterfaceMenu menu = new GateInterfaceMenu(2, player.getInventory(), pipe, Direction.UP);
    List<IStatement> iron = menu.getPotentialActions();
    check(helper, iron.contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.RED).get())
        && iron.contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.BLUE).get()),
      "An iron gate offers the red and the blue signal");
    check(helper, iron.equals(iron.stream().distinct().sorted(Comparator.comparing(s -> s.getUniqueTag().toString())).toList()),
      "The candidates are distinct and sorted by id, as the original TreeSet");

    // The client gets the same lists with the open-screen data.
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    GateInterfaceMenu.writeOpenData(buf, pipe, Direction.UP);
    GateInterfaceMenu client = new GateInterfaceMenu(3, player.getInventory(), buf);
    check(helper, client.getPotentialActions().equals(iron) && client.getPotentialTriggers().equals(menu.getPotentialTriggers()),
      "The client must receive the server's candidates");
    helper.succeed();
  }

  /** Original doSlotClick(): left = next (first when empty), right = previous, shift = clear. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateSlotClicksCycleTheCandidates(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Gate gate = attachGate(pipe, GateDefinition.GateMaterial.IRON);
    FakePlayer player = player(helper);
    GateInterfaceMenu menu = new GateInterfaceMenu(1, player.getInventory(), pipe, Direction.UP);
    List<IStatement> triggers = menu.getPotentialTriggers();
    check(helper, triggers.size() >= 3, "There must be some triggers to cycle, got " + triggers.size());

    int next = GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_TRIGGER, 1, 0, GateInterfaceMenu.CLICK_NEXT, false);
    int previous = GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_TRIGGER, 1, 0, GateInterfaceMenu.CLICK_PREVIOUS, false);
    int clear = GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_TRIGGER, 1, 0, GateInterfaceMenu.CLICK_CLEAR, false);
    menu.clickMenuButton(player, next);
    check(helper, gate.getTrigger(1) == triggers.get(0), "An empty slot takes the first candidate");
    menu.clickMenuButton(player, next);
    check(helper, gate.getTrigger(1) == triggers.get(1), "Left click takes the next candidate");
    menu.clickMenuButton(player, previous);
    menu.clickMenuButton(player, previous);
    check(helper, gate.getTrigger(1) == null, "Going back past the first candidate clears the slot");
    menu.clickMenuButton(player, previous);
    check(helper, gate.getTrigger(1) == triggers.get(triggers.size() - 1), "Right click on an empty slot takes the last candidate");
    menu.clickMenuButton(player, clear);
    check(helper, gate.getTrigger(1) == null, "Shift click clears the slot");
    check(helper, gate.getTrigger(0) == null, "Other rows stay untouched");
    check(helper, !menu.clickMenuButton(player, GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_TRIGGER, 2, 0, GateInterfaceMenu.CLICK_NEXT, false)),
      "An iron gate has two rows only");
    helper.succeed();
  }

  /** A parameter slot is clicked with the item in hand; the statement decides what the click does. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateParameterClickReachesTheParameter(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Gate gate = attachGate(pipe, GateDefinition.GateMaterial.QUARTZ);
    FakePlayer player = player(helper);
    GateInterfaceMenu menu = new GateInterfaceMenu(1, player.getInventory(), pipe, Direction.UP);
    gate.setAction(0, DefaultActionProvider.actionRedstone);
    menu.setCarried(new ItemStack(Items.STICK));

    check(helper, menu.clickMenuButton(player, GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_ACTION_PARAMETER, 0, 0, 0, false)),
      "The redstone output's side-only parameter must take a click");
    check(helper, gate.getActionParameter(0, 0) instanceof StatementParameterRedstoneGateSideOnly sideOnly && sideOnly.isOn,
      "The click must switch the parameter on");
    check(helper, !menu.clickMenuButton(player, GateInterfaceMenu.buttonId(GateInterfaceMenu.KIND_ACTION_PARAMETER, 0, 1, 0, false)),
      "A quartz gate has one action parameter slot");
    check(helper, menu.getCarried().is(Items.STICK), "Clicking a parameter must not take the held item");
    helper.succeed();
  }

  /** Original ContainerGateInterface actionsState: the lamp of an active row. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 60)
  public void gateMenuReportsActiveRows(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Gate gate = attachGate(pipe, GateDefinition.GateMaterial.IRON);
    gate.setTrigger(1, TransportStatements.TRIGGER_PIPE_CONTENTS.get(com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents.empty).get());
    gate.setAction(1, DefaultActionProvider.actionRedstone);
    GateInterfaceMenu menu = new GateInterfaceMenu(1, player(helper).getInventory(), pipe, Direction.UP);
    helper.runAfterDelay(4, () -> {
      check(helper, menu.getActionState(1) == Gate.ActionActiveState.Activated && menu.getActionState(0) == Gate.ActionActiveState.Deactivated,
        "Row 2 must be reported active, row 1 not: " + menu.getActionState(0) + " / " + menu.getActionState(1));
      helper.succeed();
    });
  }

  private static TankBlockEntity tank(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos, FactoryBlocks.TANK.get().defaultBlockState());
    return (TankBlockEntity) helper.getBlockEntity(pos);
  }

  /** Original PipeFluidsWood: no energy, no fluid (it used to pull for free every 20 ticks). */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void woodenFluidPipeNeedsEnergy(GameTestHelper helper) {
    TankBlockEntity source = tank(helper, PIPE.west());
    source.tank.fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.FLUID, PipeMaterial.WOOD);
    check(helper, pipe.getExtractionSide() == Direction.WEST, "The pipe must face the tank");
    check(helper, pipe.getBattery() instanceof FluidExtractionBattery, "The wooden fluid pipe keeps the fluid it may pull");
    helper.runAfterDelay(60, () -> {
      check(helper, source.tank.getFluidAmount() == 4000 && pipe.getFluidTank().isEmpty(), "Without energy nothing may be pulled");
      helper.succeed();
    });
  }

  /**
   * Original PipeFluidsWood.receiveEnergy(): 50 mB per RF up to 1000 mB, pulled at the flow rate; the pipe never
   * pushes back into the tank it pulls from, and the fluid reaches the tank at the end of the line.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 300)
  public void woodenFluidPipePullsWithEnergyIntoTheNextTank(GameTestHelper helper) {
    TankBlockEntity source = tank(helper, new BlockPos(0, 2, 1));
    source.tank.fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);
    PipeBlockEntity wood = placePipe(helper, new BlockPos(1, 2, 1), PipeType.FLUID, PipeMaterial.WOOD);
    TankBlockEntity target = tank(helper, new BlockPos(2, 2, 1));
    var energy = wood.getCapability(ForgeCapabilities.ENERGY, Direction.UP).orElseThrow(IllegalStateException::new);
    check(helper, energy.receiveEnergy(100, true) == 20, "An empty buffer takes 20 RF (1000 mB / 50)");
    check(helper, energy.receiveEnergy(100, false) == 20 && ((FluidExtractionBattery) wood.getBattery()).getLiquid() == 1000,
      "20 RF give 1000 mB to pull");
    check(helper, energy.receiveEnergy(1, false) == 0, "A full buffer takes nothing");

    helper.succeedWhen(() -> {
      int moved = target.tank.getFluidAmount();
      helper.assertTrue(moved >= 200, "The next tank must fill, has " + moved + " mB, pipe " + wood.getFluidTank().getFluidAmount());
      helper.assertTrue(source.tank.getFluidAmount() + wood.getFluidTank().getFluidAmount() + moved == 4000,
        "No fluid may be lost or made");
      helper.assertTrue(source.tank.getFluidAmount() >= 3000, "At most 1000 mB may be pulled for 20 RF");
    });
  }

  /** The 2026-11-21 report: a full pipe must empty into the tank next to it. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 300)
  public void fullFluidPipeEmptiesIntoTank(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.FLUID, PipeMaterial.STONE);
    TankBlockEntity target = tank(helper, PIPE.east());
    pipe = (PipeBlockEntity) helper.getBlockEntity(PIPE);
    PipeBlockEntity finalPipe = pipe;
    int filled = pipe.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.WEST).orElseThrow(IllegalStateException::new)
      .fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
    check(helper, filled == 1000, "The pipe must take 1000 mB, took " + filled);
    helper.succeedWhen(() -> helper.assertTrue(finalPipe.getFluidTank().isEmpty() && target.tank.getFluidAmount() == 1000,
      "The pipe must empty into the tank, pipe " + finalPipe.getFluidTank().getFluidAmount() + " tank " + target.tank.getFluidAmount()));
  }

  /** Original PipeFluidsEmerald: with a filter only that fluid is pulled. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void emeraldFluidPipePullsOnlyItsFilter(GameTestHelper helper) {
    TankBlockEntity source = tank(helper, PIPE.west());
    source.tank.fill(new FluidStack(Fluids.WATER, 4000), IFluidHandler.FluidAction.EXECUTE);
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.FLUID, PipeMaterial.EMERALD);
    pipe.getEmeraldFilters().setItem(0, new ItemStack(Items.LAVA_BUCKET));
    pipe.getCapability(ForgeCapabilities.ENERGY, Direction.UP).ifPresent(e -> e.receiveEnergy(20, false));
    helper.runAfterDelay(20, () -> {
      check(helper, source.tank.getFluidAmount() == 4000, "A lava filter must not pull water");
      pipe.getEmeraldFilters().setItem(0, new ItemStack(Items.WATER_BUCKET));
      pipe.getCapability(ForgeCapabilities.ENERGY, Direction.UP).ifPresent(e -> e.receiveEnergy(20, false));
      helper.runAfterDelay(10, () -> {
        check(helper, source.tank.getFluidAmount() < 4000, "A water filter must pull water");
        helper.succeed();
      });
    });
  }

  /**
   * Original EnergyStatementProvider: a gate next to an engine offers its heat stages, next to a combustion engine
   * also fuel and coolant below 25 % / 50 % (these triggers did not exist).
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateNextToEngineOffersEngineTriggers(GameTestHelper helper) {
    helper.setBlock(PIPE.east(), com.peco2282.bcreborn.energy.EnergyBlocks.IRON_ENGINE.get().defaultBlockState());
    var engine = (com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity) helper.getBlockEntity(PIPE.east());
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Gate gate = attachGate(pipe, GateDefinition.GateMaterial.IRON);
    List<IStatement> triggers = new GateInterfaceMenu(1, player(helper).getInventory(), pipe, Direction.UP).getPotentialTriggers();
    var provider = com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.class;
    check(helper, triggers.contains(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_BLUE_ENGINE_HEAT)
        && triggers.contains(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_ENGINE_OVERHEAT)
        && triggers.contains(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_FUEL_BELOW_25)
        && triggers.contains(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_COOLANT_BELOW_50),
      "A gate next to a combustion engine must offer the engine triggers (" + provider.getSimpleName() + ")");

    var params = new com.peco2282.bcreborn.api.statements.IStatementParameter[0];
    check(helper, gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_FUEL_BELOW_25, params)
        && gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_COOLANT_BELOW_25, params),
      "An empty combustion engine is below every threshold");
    check(helper, gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_BLUE_ENGINE_HEAT, params)
        && !gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_RED_ENGINE_HEAT, params),
      "A cold engine is blue");
    engine.energyStage = com.peco2282.bcreborn.common.block.entity.EngineBlockEntity.EnergyStage.RED;
    check(helper, gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_RED_ENGINE_HEAT, params),
      "The red stage trigger follows the engine");
    engine.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.WEST).ifPresent(h ->
      h.fill(new FluidStack(Fluids.WATER, 6000), IFluidHandler.FluidAction.EXECUTE));
    check(helper, !gate.isTriggerActive(com.peco2282.bcreborn.energy.statements.EnergyStatementProvider.TRIGGER_COOLANT_BELOW_50, params),
      "6000 of 10000 mB coolant is not below 50 %");
    helper.succeed();
  }
}
