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
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.silicon.block.entity.StampingTableBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GateExpansionLightSensor;
import com.peco2282.bcreborn.transport.gates.GateExpansionRedstoneFader;
import com.peco2282.bcreborn.transport.gates.GateExpansionTimer;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.statements.ActionRedstoneFaderOutput;
import com.peco2282.bcreborn.transport.statements.TriggerClockTimer;
import com.peco2282.bcreborn.transport.statements.TriggerLightSensor;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import com.peco2282.bcreborn.transport.statements.TriggerRedstoneFaderInput;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The gate expansions ported from the original 7.1.x {@code GateExpansionTimer}, {@code GateExpansionRedstoneFader}
 * and {@code GateExpansionLightSensor}, and the Stamping Table ({@code TileStampingTable}).
 */
@GameTestHolder(BCRebornTransport.MODID)
public class GateExpansionGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 2, 1);
  private static final IStatementParameter[] NO_PARAMETERS = new IStatementParameter[0];

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper) {
    BlockState state = TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState();
    helper.setBlock(PIPE, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(PIPE)));
    return (PipeBlockEntity) helper.getBlockEntity(PIPE);
  }

  private static Gate attachGate(PipeBlockEntity pipe, Direction side) {
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND, side);
    pipe.setPipePluggable(side, new GatePluggable(gate));
    return gate;
  }

  private static List<IStatement> triggers(Gate gate) {
    return gate.getAllValidTriggers();
  }

  /** Original GateExpansionTimer: three clock triggers, each active for 5 ticks every 5 / 10 / 15 seconds. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void timerExpansionOffersClockTriggersThatPulse(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper);
    Gate gate = attachGate(pipe, Direction.UP);
    check(helper, triggers(gate).stream().noneMatch(t -> t instanceof TriggerClockTimer), "Without the expansion there is no timer");
    gate.addGateExpansion(GateExpansionTimer.INSTANCE);
    check(helper, triggers(gate).stream().filter(t -> t instanceof TriggerClockTimer).count() == TriggerClockTimer.Time.VALUES.length,
      "The timer expansion must offer one trigger per period (its addTriggers was a TODO)");

    IStatement shortTimer = TransportStatements.TRIGGER_CLOCK_TIMER.get(TriggerClockTimer.Time.SHORT).get();
    IStatement longTimer = TransportStatements.TRIGGER_CLOCK_TIMER.get(TriggerClockTimer.Time.LONG).get();
    int[] activeShort = {0};
    int[] activeLong = {0};
    // 5 s + 5 active ticks = 105 ticks per short cycle; run a little over two cycles.
    for (int tick = 0; tick < 220; tick++) {
      gate.tick();
      if (gate.isTriggerActive(shortTimer, NO_PARAMETERS)) activeShort[0]++;
      if (gate.isTriggerActive(longTimer, NO_PARAMETERS)) activeLong[0]++;
    }
    check(helper, activeShort[0] >= 10 && activeShort[0] <= 15, "The 5 s timer must be active 5 ticks per cycle, was " + activeShort[0]);
    check(helper, activeLong[0] <= 5, "The 15 s timer must fire at most once in 11 s, was active " + activeLong[0] + " ticks");

    CompoundTag saved = new CompoundTag();
    gate.writeToNBT(saved);
    check(helper, saved.toString().contains("timer"), "The expansion must be saved with the gate");
    helper.succeed();
  }

  /** Original GateExpansionRedstoneFader: "input is exactly N" triggers and "output N" actions for N = 1..15. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void faderExpansionReadsAndWritesRedstoneLevels(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper);
    Gate gate = attachGate(pipe, Direction.UP);
    gate.addGateExpansion(GateExpansionRedstoneFader.INSTANCE);
    List<IStatement> faderTriggers = triggers(gate).stream().filter(t -> t instanceof TriggerRedstoneFaderInput).toList();
    List<IStatement> faderActions = gate.getAllValidActions().stream().filter(a -> a instanceof ActionRedstoneFaderOutput).toList();
    check(helper, faderTriggers.size() == 15 && faderActions.size() == 15,
      "The fader must offer 15 triggers and 15 actions, got " + faderTriggers.size() + " / " + faderActions.size());
    check(helper, faderTriggers.stream().map(t -> ((TriggerRedstoneFaderInput) t).level).toList().containsAll(List.of(1, 15))
        && faderTriggers.stream().noneMatch(t -> ((TriggerRedstoneFaderInput) t).level == 0),
      "The levels are 1..15 as in the original (they were 0..14)");

    helper.setBlock(PIPE.east(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    check(helper, gate.isTriggerActive(TransportStatements.TRIGGER_REDSTONE_FADER_INPUT.get(14).get(), NO_PARAMETERS),
      "Input level 15 must match the level-15 trigger");
    check(helper, !gate.isTriggerActive(TransportStatements.TRIGGER_REDSTONE_FADER_INPUT.get(13).get(), NO_PARAMETERS),
      "Input level 15 must not match the level-14 trigger");
    helper.setBlock(PIPE.east(), Blocks.AIR.defaultBlockState());

    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_REDSTONE_FADER_OUTPUT.get(6).get());
    helper.runAfterDelay(4, () -> {
      check(helper, gate.getRedstoneOutput() == 7, "The level-7 action must output 7, got " + gate.getRedstoneOutput());
      check(helper, pipe.getGateRedstoneOutput(Direction.WEST) == 7, "The pipe must emit level 7 to its neighbours");
      helper.succeed();
    });
  }

  /** Original GateExpansionLightSensor: bright / dark triggers from the block light in front of the gate. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void lightSensorExpansionSeesBlockLight(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper);
    Gate gate = attachGate(pipe, Direction.UP);
    gate.addGateExpansion(GateExpansionLightSensor.INSTANCE);
    check(helper, triggers(gate).stream().filter(t -> t instanceof TriggerLightSensor).count() == 2,
      "The light sensor must offer the bright and the dark trigger");
    IStatement bright = TransportStatements.TRIGGER_LIGHT_SENSOR.getOnTrue().get();
    IStatement dark = TransportStatements.TRIGGER_LIGHT_SENSOR.getOnFalse().get();
    // The sensor reads the block light in front of the gate (above the pipe). The GameTest world is shared, so
    // the light there is set by the block itself: an opaque block has none, glowstone 15.
    helper.setBlock(PIPE.above(), Blocks.STONE.defaultBlockState());
    helper.runAfterDelay(2, () -> {
      check(helper, gate.isTriggerActive(dark, NO_PARAMETERS) && !gate.isTriggerActive(bright, NO_PARAMETERS),
        "With no light in front of the gate the dark trigger must be active");
      helper.setBlock(PIPE.above(), Blocks.GLOWSTONE.defaultBlockState());
      helper.runAfterDelay(2, () -> {
        check(helper, gate.isTriggerActive(bright, NO_PARAMETERS) && !gate.isTriggerActive(dark, NO_PARAMETERS),
          "Glowstone in front of the gate must make it bright");
        helper.succeed();
      });
    });
  }

  private static StampingTableBlockEntity placeStamper(GameTestHelper helper) {
    helper.setBlock(PIPE, SiliconBlocks.STAMPING_TABLE.get().defaultBlockState());
    return (StampingTableBlockEntity) helper.getBlockEntity(PIPE);
  }

  /** Original TileStampingTable: a single item through its one-item recipe for 400 RF per item. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void stampingTableCraftsSingleItemRecipes(GameTestHelper helper) {
    StampingTableBlockEntity table = placeStamper(helper);
    check(helper, !table.canCraft() && table.getRequiredEnergy() == 0, "An empty table has no work");
    table.setItem(0, new ItemStack(Items.OAK_LOG, 2));
    check(helper, table.canCraft() && table.getRequiredEnergy() == StampingTableBlockEntity.ENERGY_PER_ITEM,
      "A log costs 400 RF (canCraft was always false)");
    table.receiveLaserEnergy(StampingTableBlockEntity.ENERGY_PER_ITEM * 2);

    helper.succeedWhen(() -> {
      helper.assertTrue(table.getItem(0).isEmpty(), "Both logs must be used, left " + table.getItem(0));
      helper.assertTrue(table.getItem(1).is(Items.OAK_PLANKS) && table.getItem(1).getCount() == 8,
        "Two logs must give 8 planks, got " + table.getItem(1));
      helper.assertTrue(table.getEnergy() == 0, "Each log costs 400 RF, left " + table.getEnergy());
    });
  }

  /** An item without a one-item recipe goes to the output unchanged; container items go to the leftover slots. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void stampingTablePassesThroughAndKeepsLeftovers(GameTestHelper helper) {
    StampingTableBlockEntity table = placeStamper(helper);
    table.setItem(0, new ItemStack(Items.STICK, 3));
    table.receiveLaserEnergy(StampingTableBlockEntity.ENERGY_PER_ITEM);

    helper.runAfterDelay(5, () -> {
      check(helper, table.getItem(0).isEmpty() && table.getItem(1).is(Items.STICK) && table.getItem(1).getCount() == 3,
        "Sticks have no one-item recipe and must pass through whole, got " + table.getItem(1));

      // A Package: honey bottles x4 -> honey block; the four empty bottles stay behind.
      table.setItem(1, ItemStack.EMPTY);
      ItemStack pack = new ItemStack(SiliconItems.PACKAGE_ITEM.get());
      for (int i : new int[]{0, 1, 3, 4}) {
        pack.getOrCreateTag().put("item" + i, new ItemStack(Items.HONEY_BOTTLE).save(new CompoundTag()));
      }
      table.setItem(0, pack);
      check(helper, table.getRequiredEnergy() == 4 * StampingTableBlockEntity.ENERGY_PER_ITEM,
        "A package costs 400 RF per stack in it, got " + table.getRequiredEnergy());
      table.receiveLaserEnergy(4 * StampingTableBlockEntity.ENERGY_PER_ITEM);
      helper.runAfterDelay(5, () -> {
        check(helper, table.getItem(1).is(Items.HONEY_BLOCK), "The package must be crafted as its grid, got " + table.getItem(1));
        int bottles = 0;
        for (int slot = 2; slot <= 4; slot++) {
          if (table.getItem(slot).is(Items.GLASS_BOTTLE)) bottles += table.getItem(slot).getCount();
        }
        check(helper, bottles == 4, "The empty bottles must go to the leftover slots, got " + bottles);
        check(helper, table.getItem(0).isEmpty(), "The package is used up");
        helper.succeed();
      });
    });
  }
}
