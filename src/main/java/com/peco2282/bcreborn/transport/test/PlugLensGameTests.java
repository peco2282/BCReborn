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
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.PipeColorLogic;
import com.peco2282.bcreborn.transport.pipe.pluggable.FacadePluggable;
import net.minecraft.world.level.block.Blocks;
import com.peco2282.bcreborn.transport.pipe.pluggable.LensPluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.PlugPluggable;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * Verifies the pipe plug and the lens / filter ported from the original 7.1.x {@code PlugPluggable},
 * {@code LensPluggable} and {@code LensFilterHandler}, and the data the fluid pipe renderer relies on.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class PlugLensGameTests {
  private static final BlockPos CENTER = new BlockPos(1, 1, 1);
  private static final BlockPos WEST = new BlockPos(0, 1, 1);
  private static final BlockPos EAST = new BlockPos(2, 1, 1);
  private static final BlockPos NORTH = new BlockPos(1, 1, 0);
  private static final BlockPos SOUTH = new BlockPos(1, 1, 2);
  /** Routing is random, so "never" and "always" are checked over this many items. */
  private static final int ROUTING_SAMPLES = 40;

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeType type, PipeMaterial material) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    return placePipe(helper, pos, PipeType.ITEM, material);
  }

  /** A centre pipe with a pipe on each horizontal side. */
  private static PipeBlockEntity placeCross(GameTestHelper helper, PipeMaterial centerMaterial) {
    PipeBlockEntity center = placePipe(helper, CENTER, centerMaterial);
    for (BlockPos pos : new BlockPos[]{WEST, EAST, NORTH, SOUTH}) {
      placePipe(helper, pos, PipeMaterial.COBBLESTONE);
    }
    return (PipeBlockEntity) helper.getBlockEntity(CENTER);
  }

  private static boolean connected(GameTestHelper helper, BlockPos pos, Direction dir) {
    return PipeBlock.isConnected(helper.getBlockState(pos), dir);
  }

  /**
   * Injects an item from the west into the centre pipe and returns the side it was routed to. The item is removed
   * again, so the pipe never fills up.
   */
  @Nullable
  private static Direction route(PipeBlockEntity pipe, @Nullable DyeColor color) {
    pipe.injectItemWithSpeed(new ItemStack(Items.COBBLESTONE), Direction.WEST, 0.01f, color);
    List<TravelingItem> items = pipe.getTravelingItems();
    Direction next = items.get(items.size() - 1).getNextDirection();
    // Reload the pipe's own data with an empty item list.
    CompoundTag tag = pipe.saveWithFullMetadata();
    tag.put("TravelingItems", new ListTag());
    pipe.getRuntime().load(tag);
    return next;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void plugClosesTheConnectionOnBothPipes(GameTestHelper helper) {
    PipeBlockEntity a = placePipe(helper, WEST, PipeMaterial.COBBLESTONE);
    placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    check(helper, connected(helper, WEST, Direction.EAST) && connected(helper, CENTER, Direction.WEST),
      "Two item pipes must connect before the plug is installed");

    a.setPipePluggable(Direction.EAST, new PlugPluggable());
    check(helper, a.hasBlockingPluggable(Direction.EAST), "A plug must block its side");
    check(helper, !connected(helper, WEST, Direction.EAST), "The plugged side must not be connected");
    check(helper, !connected(helper, CENTER, Direction.WEST), "The neighbour must not connect to a plugged side");

    ItemStack[] drops = a.getPipePluggable(Direction.EAST).getDropItems(a);
    check(helper, drops.length == 1 && drops[0].is(TransportItems.PIPE_PLUG.get()), "A plug must drop the plug item");

    a.setPipePluggable(Direction.EAST, null);
    check(helper, connected(helper, WEST, Direction.EAST) && connected(helper, CENTER, Direction.WEST),
      "Removing the plug must restore the connection");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void plugSeparatesWireNetworks(GameTestHelper helper) {
    PipeBlockEntity a = placePipe(helper, WEST, PipeMaterial.COBBLESTONE);
    PipeBlockEntity b = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    a.addWire(PipeWire.RED);
    b.addWire(PipeWire.RED);
    check(helper, a.isWireConnected(PipeWire.RED, Direction.EAST), "Wires on connected pipes must join");
    b.setPipePluggable(Direction.WEST, new PlugPluggable());
    check(helper, !a.isWireConnected(PipeWire.RED, Direction.EAST), "A plug must separate the wires (original Pipe.isWireConnectedTo)");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void plugItemIsInstalledAndStrippedByHand(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "plug"));
    BlockPos abs = helper.absolutePos(CENTER);
    player.setPos(abs.getX() + 0.5, abs.getY() + 2, abs.getZ() + 0.5);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.25, 0), Direction.UP, abs, false);

    ItemStack plugs = new ItemStack(TransportItems.PIPE_PLUG.get(), 2);
    player.setItemInHand(InteractionHand.MAIN_HAND, plugs);
    helper.getBlockState(CENTER).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    check(helper, pipe.getPipePluggable(Direction.UP) instanceof PlugPluggable, "Using a plug on a pipe must install it on the clicked side");
    check(helper, plugs.getCount() == 1, "Installing a plug must use up one item");

    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    player.setShiftKeyDown(true);
    helper.getBlockState(CENTER).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    check(helper, pipe.getPipePluggable(Direction.UP) == null, "Sneaking with an empty hand must strip the plug");
    check(helper, player.getInventory().countItem(TransportItems.PIPE_PLUG.get()) == 1, "The stripped plug must go to the player");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void lensPaintsItemsLeavingThroughIt(GameTestHelper helper) {
    PipeBlockEntity a = placePipe(helper, WEST, PipeMaterial.COBBLESTONE);
    PipeBlockEntity b = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    a.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.RED, false));
    check(helper, connected(helper, WEST, Direction.EAST), "A lens must not close the connection");
    a.injectItem(new ItemStack(Items.COBBLESTONE), Direction.WEST);
    check(helper, a.getTravelingItems().get(0).getColor() == null, "A lens on the far side must not paint the item on entry");

    helper.succeedWhen(() -> {
      List<TravelingItem> items = b.getTravelingItems();
      check(helper, !items.isEmpty(), "Item has not reached the next pipe yet");
      check(helper, items.get(0).getColor() == DyeColor.RED, "The lens must paint the item red, got " + items.get(0).getColor());
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void lensPaintsItemsEnteringThroughItAndClearLensRemovesPaint(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    placePipe(helper, EAST, PipeMaterial.COBBLESTONE);
    pipe.setPipePluggable(Direction.WEST, new LensPluggable(DyeColor.BLUE, false));
    pipe.injectItem(new ItemStack(Items.COBBLESTONE), Direction.WEST);
    check(helper, pipe.getTravelingItems().get(0).getColor() == DyeColor.BLUE, "A lens on the entry side must paint the item");

    pipe.setPipePluggable(Direction.WEST, new LensPluggable(null, false));
    pipe.injectItemWithSpeed(new ItemStack(Items.COBBLESTONE), Direction.WEST, 0.01f, DyeColor.RED);
    check(helper, pipe.getTravelingItems().get(1).getColor() == null, "A clear lens must remove the paint");

    pipe.setPipePluggable(Direction.WEST, new LensPluggable(DyeColor.GREEN, true));
    pipe.injectItemWithSpeed(new ItemStack(Items.COBBLESTONE), Direction.WEST, 0.01f, DyeColor.RED);
    check(helper, pipe.getTravelingItems().get(2).getColor() == DyeColor.RED, "A filter must not repaint the item");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filterTakesOnlyItsColourAndMatchingItemsPreferIt(GameTestHelper helper) {
    PipeBlockEntity pipe = placeCross(helper, PipeMaterial.COBBLESTONE);
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.RED, true));
    check(helper, connected(helper, CENTER, Direction.EAST), "A filter must not close the connection");

    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      Direction plain = route(pipe, null);
      check(helper, plain == Direction.NORTH || plain == Direction.SOUTH, "An unpainted item must avoid the red filter, went " + plain);
      Direction blue = route(pipe, DyeColor.BLUE);
      check(helper, blue == Direction.NORTH || blue == Direction.SOUTH, "A blue item must avoid the red filter, went " + blue);
      Direction red = route(pipe, DyeColor.RED);
      check(helper, red == Direction.EAST, "A red item must take the red filter over unfiltered sides, went " + red);
    }

    // Original clear filter: matches unpainted items.
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(null, true));
    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      check(helper, route(pipe, null) == Direction.EAST, "An unpainted item must take the clear filter");
      check(helper, route(pipe, DyeColor.RED) != Direction.EAST, "A painted item must avoid the clear filter");
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filterOnTheNextPipeAndLensInFrontOfIt(GameTestHelper helper) {
    PipeBlockEntity pipe = placeCross(helper, PipeMaterial.COBBLESTONE);
    PipeBlockEntity east = (PipeBlockEntity) helper.getBlockEntity(EAST);
    east.setPipePluggable(Direction.WEST, new LensPluggable(DyeColor.BLUE, true));
    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      check(helper, route(pipe, DyeColor.BLUE) == Direction.EAST, "A filter on the next pipe's facing side must attract its colour");
      check(helper, route(pipe, null) != Direction.EAST, "A filter on the next pipe's facing side must refuse other items");
    }

    // Two filters of different colours close the side for everything.
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.RED, true));
    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      check(helper, route(pipe, DyeColor.BLUE) != Direction.EAST && route(pipe, DyeColor.RED) != Direction.EAST,
        "Filters of different colours on both pipes must close the side");
    }

    // A lens of another colour in front of the filter closes the side as well ...
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.RED, false));
    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      check(helper, route(pipe, DyeColor.BLUE) != Direction.EAST && route(pipe, null) != Direction.EAST,
        "A lens of another colour in front of a filter must close the side");
    }

    // ... while a lens of the filter's colour makes it an ordinary side (the lens paints the item on the way out).
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.BLUE, false));
    boolean wentEast = false;
    for (int i = 0; i < ROUTING_SAMPLES * 4 && !wentEast; i++) {
      wentEast = route(pipe, null) == Direction.EAST;
    }
    check(helper, wentEast, "A lens of the filter's colour must leave the side open to unpainted items");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPipeBouncesItemsItsFilteredOutputRefuses(GameTestHelper helper) {
    PipeBlockEntity pipe = placeCross(helper, PipeMaterial.IRON);
    check(helper, IronPipeLogic.setFacing(pipe, Direction.EAST), "EAST must be a valid iron output");
    pipe.setPipePluggable(Direction.EAST, new LensPluggable(DyeColor.RED, true));
    for (int i = 0; i < ROUTING_SAMPLES; i++) {
      check(helper, route(pipe, DyeColor.RED) == Direction.EAST, "A red item must leave through the red-filtered iron output");
      Direction plain = route(pipe, null);
      check(helper, plain == Direction.WEST, "An item the output filter refuses must bounce back (original allowBouncing), went " + plain);
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void lensSurvivesSaveAndDropsItsOwnItem(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    pipe.setPipePluggable(Direction.NORTH, new LensPluggable(DyeColor.LIME, true));
    pipe.setPipePluggable(Direction.SOUTH, new LensPluggable(null, false));
    pipe.setPipePluggable(Direction.UP, new PlugPluggable());

    CompoundTag saved = pipe.saveWithFullMetadata();
    PipeBlockEntity copy = placePipe(helper, EAST, PipeMaterial.COBBLESTONE);
    copy.load(saved);

    PipePluggable<?> north = copy.getPipePluggable(Direction.NORTH);
    check(helper, north instanceof LensPluggable lens && lens.isFilter() && lens.getColor() == DyeColor.LIME, "A lime filter must be read back as a lime filter");
    PipePluggable<?> south = copy.getPipePluggable(Direction.SOUTH);
    check(helper, south instanceof LensPluggable lens && !lens.isFilter() && lens.getColor() == null, "A clear lens must be read back as a clear lens");
    check(helper, copy.getPipePluggable(Direction.UP) instanceof PlugPluggable, "A plug must be read back as a plug");
    check(helper, north.getDropItems(copy)[0].is(TransportItems.lens(DyeColor.LIME, true)), "A lime filter must drop the lime filter item");
    check(helper, south.getDropItems(copy)[0].is(TransportItems.PIPE_LENS_CLEAR.get()), "A clear lens must drop the clear lens item");
    check(helper, PipeManager.hasPipePluggable(BCRebornTransport.PLUG.id()) && PipeManager.hasPipePluggable(BCRebornTransport.LENS.id()),
      "Plug and lens must be registered pluggable types");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void everyColourHasALensAndAFilterItem(GameTestHelper helper) {
    for (DyeColor color : DyeColor.values()) {
      for (boolean filter : new boolean[]{false, true}) {
        var item = TransportItems.lens(color, filter);
        check(helper, item.getColor() == color && item.isFilter() == filter, "Wrong lens item for " + color + " filter=" + filter);
        var pluggable = item.createPipePluggable(null, Direction.UP, new ItemStack(item));
        check(helper, pluggable instanceof LensPluggable lens && lens.getColor() == color && lens.isFilter() == filter,
          "The lens item must create its own pluggable for " + color + " filter=" + filter);
      }
    }
    helper.succeed();
  }

  /**
   * The fluid renderer draws from the synced data: the direction states and what passed through in the last tick
   * (the tank itself is usually empty again when the client hears about it).
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void fluidPipeSyncsFlowForRendering(GameTestHelper helper) {
    PipeBlockEntity first = placePipe(helper, WEST, PipeType.FLUID, PipeMaterial.STONE);
    placePipe(helper, CENTER, PipeType.FLUID, PipeMaterial.STONE);
    FluidTransportModule module = first.getFluidTransportModule();
    check(helper, module.getFlowFluid().isEmpty(), "An idle pipe must not report a flow");
    first.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.WEST).orElseThrow(IllegalStateException::new)
      .fill(new FluidStack(Fluids.WATER, 100), FluidAction.EXECUTE);

    helper.startSequence()
      .thenWaitUntil(() -> {
        check(helper, !module.getFlowFluid().isEmpty(), "No flow reported yet");
        check(helper, module.getFlowFluid().getFluid() == Fluids.WATER, "The flow must be water");
        check(helper, module.getTransferState(Direction.WEST) == FluidTransportModule.TransferState.Input, "The filled side must be an input");
        check(helper, module.getTransferState(Direction.EAST) == FluidTransportModule.TransferState.Output, "The side towards the next pipe must be an output");
        CompoundTag update = first.getUpdateTag().getCompound("FluidModule");
        check(helper, update.contains("flow"), "The flow must be part of the client update");
      })
      .thenWaitUntil(() -> {
        check(helper, first.getFluidTank().isEmpty(), "The pipe has not emptied yet");
        check(helper, module.getFlowFluid().isEmpty(), "A pipe that moved nothing must stop reporting a flow");
        check(helper, !first.getUpdateTag().getCompound("FluidModule").contains("flow"), "The stopped flow must be synced too");
      })
      .thenSucceed();
  }

  /** A phased facade shows the state of the first active wire, otherwise its default state. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void phasedFacadeFollowsItsWire(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, CENTER, PipeMaterial.COBBLESTONE);
    placePipe(helper, EAST, PipeMaterial.COBBLESTONE);
    pipe.addWire(PipeWire.RED);
    FacadeItem.FacadeState[] states = {
      new FacadeItem.FacadeState(Blocks.STONE.defaultBlockState(), null, false),
      new FacadeItem.FacadeState(null, PipeWire.RED, true)
    };
    FacadePluggable facade = new FacadePluggable(states);
    pipe.setPipePluggable(Direction.EAST, facade);
    check(helper, facade.getState().is(Blocks.STONE) && !facade.isTransparent(), "Without a signal the default state is shown");
    check(helper, !connected(helper, CENTER, Direction.EAST), "A solid facade state closes the side");

    // A gate whose rule (true while the pipe is empty) broadcasts the red signal.
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, Direction.UP);
    pipe.setPipePluggable(Direction.UP, new GatePluggable(gate));
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.RED).get());

    helper.startSequence()
      .thenWaitUntil(() -> {
        check(helper, pipe.isWireActive(PipeWire.RED), "The wire is not active yet");
        check(helper, facade.isTransparent() && facade.isHollow(), "With the red wire active the transparent hollow state must be shown");
        check(helper, connected(helper, CENTER, Direction.EAST), "A hollow facade state opens the side again");
        PipePluggable<?> synced = PipeManager.createPipePluggable(BCRebornTransport.FACADE.id(), pluggableTag(pipe, Direction.EAST));
        check(helper, synced instanceof FacadePluggable copy && copy.isTransparent() && copy.getStates().length == 2,
          "The active state and all states must be saved and synced");
      })
      .thenExecute(() -> pipe.setPipePluggable(Direction.UP, null))
      .thenWaitUntil(() -> check(helper, facade.getState().is(Blocks.STONE) && !connected(helper, CENTER, Direction.EAST),
        "Without the signal the facade must return to its default state"))
      .thenExecute(() -> {
        ItemStack drop = facade.getDropItems(pipe)[0];
        check(helper, FacadeItem.getStates(drop).length == 2, "A phased facade must drop with all its states");
      })
      .thenSucceed();
  }

  private static CompoundTag pluggableTag(PipeBlockEntity pipe, Direction side) {
    return pipe.getUpdateTag().getCompound("SideProperties").getCompound("pluggable[" + side.ordinal() + "]");
  }

  /** Original PipeTransportStructure: connects to structure pipes only, but carries wires to any pipe. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void structurePipeConnectsOnlyToItsKindButCarriesWires(GameTestHelper helper) {
    PipeBlockEntity item = placePipe(helper, WEST, PipeMaterial.COBBLESTONE);
    PipeBlockEntity structure = placePipe(helper, CENTER, PipeType.STRUCTURE, PipeMaterial.COBBLESTONE);
    PipeBlockEntity structure2 = placePipe(helper, EAST, PipeType.STRUCTURE, PipeMaterial.COBBLESTONE);
    helper.setBlock(NORTH, Blocks.CHEST);
    check(helper, !connected(helper, CENTER, Direction.WEST) && !connected(helper, WEST, Direction.EAST),
      "A structure pipe must not connect to an item pipe");
    check(helper, connected(helper, CENTER, Direction.EAST) && connected(helper, EAST, Direction.WEST),
      "Structure pipes must connect to each other");
    check(helper, !connected(helper, CENTER, Direction.NORTH), "A structure pipe must not connect to an inventory");
    check(helper, !structure.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).isPresent(), "A structure pipe carries nothing");

    item.addWire(PipeWire.GREEN);
    structure.addWire(PipeWire.GREEN);
    structure2.addWire(PipeWire.GREEN);
    check(helper, item.isWireConnected(PipeWire.GREEN, Direction.EAST) && structure.isWireConnected(PipeWire.GREEN, Direction.WEST),
      "Wires must pass between a structure pipe and an unconnected neighbour pipe");
    check(helper, structure.isWireConnected(PipeWire.GREEN, Direction.EAST), "Wires must pass between structure pipes");

    structure.setPipePluggable(Direction.UP, new PlugPluggable());
    check(helper, structure.getPipePluggable(Direction.UP) instanceof PlugPluggable, "A structure pipe must hold pluggables");
    helper.succeed();
  }

  /** The Lapis / Daizuli model takes its texture from this model data. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void colourPipesExposeTheirColourToTheModel(GameTestHelper helper) {
    PipeBlockEntity lapis = placePipe(helper, CENTER, PipeMaterial.LAPIS);
    lapis.setPipeColor(DyeColor.ORANGE);
    check(helper, lapis.getModelData().get(PipeBlockEntity.PIPE_COLOR) == DyeColor.ORANGE, "A Lapis pipe must expose its colour as model data");
    PipeBlockEntity daizuli = placePipe(helper, EAST, PipeMaterial.DAIZULI);
    check(helper, daizuli.getModelData().get(PipeBlockEntity.PIPE_COLOR) == PipeColorLogic.colorOf(daizuli), "A Daizuli pipe must expose its default colour");
    PipeBlockEntity plain = placePipe(helper, WEST, PipeMaterial.COBBLESTONE);
    check(helper, !plain.getModelData().has(PipeBlockEntity.PIPE_COLOR), "Other pipes have no colour model data");
    helper.succeed();
  }
}
