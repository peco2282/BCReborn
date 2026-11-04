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
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.core.statements.ActionMachineControl;
import com.peco2282.bcreborn.core.statements.ActionRedstoneOutput;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies that gates offer and apply statements like the original 7.1.x {@code Gate}, {@code PipeTriggerProvider},
 * {@code PipeActionProvider} and {@code Pipe.isPoweringTo()}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class GateGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, PipeMaterial material) {
    BlockPos absolute = helper.absolutePos(PIPE);
    BlockState state = TransportBlocks.get(PipeType.ITEM, material).get().defaultBlockState();
    helper.setBlock(PIPE, Block.updateFromNeighbourShapes(state, helper.getLevel(), absolute));
    return (PipeBlockEntity) helper.getBlockEntity(PIPE);
  }

  /** Attaches a redstone AND gate on {@code side}, as placing a gate item does. */
  private static Gate attachGate(PipeBlockEntity pipe, Direction side) {
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, side);
    pipe.setPipePluggable(side, new GatePluggable(gate));
    return gate;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateOffersCoreAndPipeStatements(GameTestHelper helper) {
    helper.setBlock(PIPE.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(PIPE.east(), BuildersBlock.QUARRY.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.EAST));
    PipeBlockEntity pipe = placePipe(helper, PipeMaterial.IRON);
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, Direction.UP);

    List<IStatement> actions = gate.getAllValidActions();
    check(helper, actions.stream().anyMatch(a -> a instanceof ActionRedstoneOutput),
      "Gates must offer the redstone output action (DefaultActionProvider was never registered)");
    check(helper, actions.stream().anyMatch(a -> a instanceof ActionMachineControl),
      "A gate next to a quarry must offer machine control (Off / On / Loop)");
    check(helper, actions.contains(TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.NORTH).get()),
      "An iron pipe must offer a direction action for its connected side");
    check(helper, !actions.contains(TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.DOWN).get()),
      "No direction action for an unconnected side");

    List<IStatement> triggers = gate.getAllValidTriggers();
    check(helper, triggers.contains(TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get())
        && triggers.contains(TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.containsItems).get()),
      "An item pipe must offer the pipe contents triggers");
    check(helper, !triggers.contains(TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.containsFluids).get()),
      "An item pipe must not offer fluid triggers");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateActionSetsIronPipeDirection(GameTestHelper helper) {
    helper.setBlock(PIPE.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(PIPE.east(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PipeMaterial.IRON);
    check(helper, pipe.getIronPipeOutput() == Direction.NORTH, "Placement must choose NORTH first");

    Gate gate = attachGate(pipe, Direction.UP);
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_PIPE_DIRECTION.get(Direction.EAST).get());

    helper.runAfterDelay(2, () -> {
      // Previously actionsActivated() was commented out, so the action had no effect.
      check(helper, pipe.getIronPipeOutput() == Direction.EAST,
        "An active pipe direction action must set the iron pipe output, got " + pipe.getIronPipeOutput());
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateRedstoneOutputPowersNeighbour(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PipeMaterial.COBBLESTONE);
    helper.setBlock(PIPE.west(), Blocks.REDSTONE_LAMP.defaultBlockState());

    Gate gate = attachGate(pipe, Direction.UP);
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    IStatement redstone = gate.getAllValidActions().stream()
      .filter(a -> a instanceof ActionRedstoneOutput).findFirst().orElse(null);
    check(helper, redstone != null, "Redstone output action must be available");
    gate.setAction(0, redstone);

    helper.runAfterDelay(4, () -> {
      check(helper, gate.getRedstoneOutput() > 0, "The active gate must output redstone");
      check(helper, helper.getBlockState(PIPE.west()).getValue(RedstoneLampBlock.LIT),
        "The gate's redstone output must reach and light the lamp next to the pipe");

      // Trigger stops holding -> output turns off.
      gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.containsItems).get());
      helper.runAfterDelay(8, () -> {
        check(helper, gate.getRedstoneOutput() == 0, "The gate must stop its redstone output");
        check(helper, !helper.getBlockState(PIPE.west()).getValue(RedstoneLampBlock.LIT),
          "The lamp must turn off when the gate output stops");
        helper.succeed();
      });
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void machineControlActionReachesQuarry(GameTestHelper helper) {
    helper.setBlock(PIPE.east(), BuildersBlock.QUARRY.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.EAST));
    PipeBlockEntity pipe = placePipe(helper, PipeMaterial.COBBLESTONE);
    Gate gate = attachGate(pipe, Direction.UP);
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    IStatement off = gate.getAllValidActions().stream()
      .filter(a -> a instanceof ActionMachineControl control && control.mode == IControllable.Mode.Off)
      .findFirst().orElse(null);
    check(helper, off != null, "Machine control Off must be available next to a quarry");
    gate.setAction(0, off);

    helper.runAfterDelay(3, () -> {
      var quarry = (com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity) helper.getBlockEntity(PIPE.east());
      check(helper, quarry.getControlMode() == IControllable.Mode.Off, "The gate must switch the quarry Off");
      helper.succeed();
    });
  }
}
