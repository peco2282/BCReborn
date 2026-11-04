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
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.runtime.PipeRuntime;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies pipe wires ported from the original 7.1.x {@code Pipe} (wireSet / wireSignalStrength /
 * updateSignalState) and {@code BlockGenericPipe} (addOrStripWire / stripEquipment).
 * A row of three pipes at x = 0..2 (y = 1, z = 1) is used.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class PipeWireGameTests {
  private static final BlockPos A = new BlockPos(0, 1, 1);
  private static final BlockPos B = new BlockPos(1, 1, 1);
  private static final BlockPos C = new BlockPos(2, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos) {
    BlockState state = TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static PipeBlockEntity[] wiredRow(GameTestHelper helper, PipeWire wire) {
    PipeBlockEntity[] row = {placePipe(helper, A), placePipe(helper, B), placePipe(helper, C)};
    for (PipeBlockEntity pipe : row) {
      pipe.addWire(wire);
    }
    return row;
  }

  /** A gate whose (always true while the pipe is empty) rule broadcasts {@code wire}. */
  private static Gate broadcastingGate(PipeBlockEntity pipe, PipeWire wire) {
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, Direction.UP);
    pipe.setPipePluggable(Direction.UP, new GatePluggable(gate));
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, TransportStatements.ACTION_SIGNAL_OUTPUT.get(wire).get());
    return gate;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void wireItemInstallsAndSneakStrips(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, B);
    var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "WireTest"));
    BlockPos abs = helper.absolutePos(B);
    player.setPos(abs.getX() + 0.5, abs.getY() + 2, abs.getZ() + 0.5);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs).add(0, 0.25, 0), Direction.UP, abs, false);

    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(TransportItems.PIPE_WIRE_BLUE.get(), 2));
    helper.getBlockState(B).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    check(helper, pipe.isWired(PipeWire.BLUE) && !pipe.isWired(PipeWire.RED), "A blue wire item must install only the blue wire");
    check(helper, player.getMainHandItem().getCount() == 1, "Installing must consume one wire");

    helper.getBlockState(B).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    check(helper, player.getMainHandItem().getCount() == 1, "A second click on an installed wire must not consume more");

    player.setShiftKeyDown(true);
    helper.getBlockState(B).use(helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    check(helper, !pipe.isWired(PipeWire.BLUE), "Sneak + wire on an installed wire must strip it");
    check(helper, player.getInventory().countItem(TransportItems.PIPE_WIRE_BLUE.get()) == 2,
      "The stripped wire must go back to the player");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void gateBroadcastPropagatesWithDecreasingStrength(GameTestHelper helper) {
    PipeBlockEntity[] row = wiredRow(helper, PipeWire.RED);
    broadcastingGate(row[0], PipeWire.RED);

    helper.runAfterDelay(3, () -> {
      check(helper, row[0].getWireSignalStrength(PipeWire.RED) == PipeRuntime.MAX_WIRE_STRENGTH,
        "The broadcasting pipe must be at full strength");
      check(helper, row[1].getWireSignalStrength(PipeWire.RED) == PipeRuntime.MAX_WIRE_STRENGTH - 1
          && row[2].getWireSignalStrength(PipeWire.RED) == PipeRuntime.MAX_WIRE_STRENGTH - 2,
        "Strength must drop by one per pipe (original Pipe.updateSignalState)");
      check(helper, !row[2].isWireActive(PipeWire.BLUE), "Other colours must stay inactive");

      // Cut the network in the middle: the far pipe loses the signal.
      row[1].removeWire(PipeWire.RED);
      helper.runAfterDelay(2, () -> {
        check(helper, !row[2].isWireActive(PipeWire.RED), "Removing a wire must cut the signal behind it");
        check(helper, row[0].isWireActive(PipeWire.RED), "The source side must stay active");
        helper.succeed();
      });
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void secondSourceKeepsNetworkActive(GameTestHelper helper) {
    PipeBlockEntity[] row = wiredRow(helper, PipeWire.GREEN);
    Gate first = broadcastingGate(row[0], PipeWire.GREEN);
    broadcastingGate(row[2], PipeWire.GREEN);

    helper.runAfterDelay(3, () -> {
      // Stop the first source; the second one must still power the whole network.
      first.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.containsItems).get());
      helper.runAfterDelay(3, () -> {
        check(helper, row[0].getWireSignalStrength(PipeWire.GREEN) == PipeRuntime.MAX_WIRE_STRENGTH - 2,
          "The remaining source must reach the far end, got " + row[0].getWireSignalStrength(PipeWire.GREEN));
        helper.succeed();
      });
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void wireStatementsAndDrops(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, B);
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.REDSTONE, GateDefinition.GateLogic.AND, Direction.UP);
    List<IStatement> before = gate.getAllValidActions();
    check(helper, !before.contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.RED).get()),
      "Without a wire there must be no wire action");

    pipe.addWire(PipeWire.RED);
    pipe.addWire(PipeWire.BLUE);
    check(helper, gate.getAllValidActions().contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.RED).get()),
      "A wired pipe must offer the wire signal action");
    check(helper, gate.getAllValidTriggers().contains(TransportStatements.TRIGGER_PIPE_SIGNAL.get(true, PipeWire.RED).get()),
      "A wired pipe must offer the wire signal trigger");
    // Redstone gates only handle the first wire colour (original GateMaterial.maxWireColor).
    check(helper, !gate.getAllValidActions().contains(TransportStatements.ACTION_SIGNAL_OUTPUT.get(PipeWire.BLUE).get()),
      "A redstone gate must not offer a second wire colour");

    helper.setBlock(B, Blocks.AIR.defaultBlockState());
    BlockPos abs = helper.absolutePos(B);
    long wires = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(abs).inflate(2)).stream()
      .filter(e -> PipeWire.fromStack(e.getItem()) != null).mapToInt(e -> e.getItem().getCount()).sum();
    check(helper, wires == 2, "Breaking the pipe must drop its installed wires, got " + wires);
    helper.succeed();
  }
}
