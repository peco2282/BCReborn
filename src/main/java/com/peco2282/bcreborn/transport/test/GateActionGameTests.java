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
import com.peco2282.bcreborn.api.transport.PowerMode;
import com.peco2282.bcreborn.core.statements.StatementParameterDirection;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportStatements;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.statements.ActionValve.ValveState;
import com.peco2282.bcreborn.transport.statements.TriggerPipeContents.PipeContents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the remaining pipe gate actions ported from the original 7.1.x {@code ActionValve},
 * {@code PipeItemsLapis} and {@code PipePowerIron}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class GateActionGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, PipeType type, PipeMaterial material) {
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    helper.setBlock(PIPE, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(PIPE)));
    return (PipeBlockEntity) helper.getBlockEntity(PIPE);
  }

  /** A gate on UP whose rule is always active while the pipe is empty. */
  private static Gate gateWithAction(PipeBlockEntity pipe, IStatement action) {
    Gate gate = new Gate(pipe.getPipe(), GateDefinition.GateMaterial.IRON, GateDefinition.GateLogic.AND, Direction.UP);
    pipe.setPipePluggable(Direction.UP, new GatePluggable(gate));
    gate.setTrigger(0, TransportStatements.TRIGGER_PIPE_CONTENTS.get(PipeContents.empty).get());
    gate.setAction(0, action);
    return gate;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void lapisColourAction(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PipeType.ITEM, PipeMaterial.LAPIS);
    Gate gate = gateWithAction(pipe, TransportStatements.ACTION_PIPE_COLOR.get(DyeColor.RED).get());
    List<IStatement> actions = gate.getAllValidActions();
    check(helper, actions.contains(TransportStatements.ACTION_PIPE_COLOR.get(DyeColor.LIME).get()),
      "A lapis pipe must offer the pipe colour actions");
    helper.runAfterDelay(2, () -> {
      check(helper, pipe.getPipeColor() == DyeColor.RED, "The colour action must recolour the lapis pipe, got " + pipe.getPipeColor());
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPowerLimiterAction(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PipeType.ENERGY, PipeMaterial.IRON);
    Gate gate = gateWithAction(pipe, TransportStatements.ACTION_POWER_LIMITER.get(PowerMode.M32).get());
    check(helper, gate.getAllValidActions().contains(TransportStatements.ACTION_POWER_LIMITER.get(PowerMode.M2).get()),
      "An iron power pipe must offer the power limiter actions");
    helper.runAfterDelay(3, () -> {
      check(helper, pipe.getIronPipeEnergyLimit() == 320, "M32 must limit the pipe to 320 RF/t, got " + pipe.getIronPipeEnergyLimit());
      check(helper, pipe.getEnergyTransportModule().getMaxPower() == 320, "The transport module must use the limit");
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void valveClosesOutputsAndInputs(GameTestHelper helper) {
    helper.setBlock(PIPE.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(PIPE.south(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PipeType.ITEM, PipeMaterial.COBBLESTONE);
    Gate gate = gateWithAction(pipe, TransportStatements.ACTION_VALVE.get(ValveState.INPUT_ONLY).get());
    check(helper, gate.getAllValidActions().contains(TransportStatements.ACTION_VALVE.get(ValveState.CLOSED).get()),
      "Every pipe must offer the valve actions");

    helper.runAfterDelay(2, () -> {
      check(helper, pipe.isInputOpen(Direction.NORTH) && !pipe.isOutputOpen(Direction.NORTH),
        "INPUT_ONLY must close every output and keep inputs open");
      pipe.injectItem(new ItemStack(Items.COBBLESTONE), Direction.NORTH);
      TravelingItem item = pipe.getTravelingItems().get(0);
      check(helper, item.getNextDirection() == null,
        "With every output closed a cobblestone pipe has no destination (original: dropped, no bouncing), got " + item.getNextDirection());

      CompoundTag saved = pipe.saveWithoutMetadata();
      PipeBlockEntity restored = new PipeBlockEntity(pipe.getBlockPos(), pipe.getBlockState(), PipeType.ITEM, PipeMaterial.COBBLESTONE);
      restored.load(saved);
      check(helper, !restored.isOutputOpen(Direction.SOUTH) && restored.isInputOpen(Direction.SOUTH),
        "Valve state must be saved (original inputOpen / outputOpen)");

      // OUTPUT_ONLY on the east side only (direction parameter): external insertion from the east is refused.
      IStatementParameter[] params = {new StatementParameterDirection(), null, null};
      ((StatementParameterDirection) params[0]).direction = Direction.EAST;
      TransportStatements.ACTION_VALVE.get(ValveState.OUTPUT_ONLY).get().actionActivate(gate, params);
      check(helper, !pipe.isInputOpen(Direction.EAST) && pipe.isOutputOpen(Direction.EAST),
        "A direction parameter must only change that side");
      check(helper, !pipe.isOutputOpen(Direction.WEST), "Other sides must keep their previous valve state");
      var handler = pipe.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.EAST).orElse(null);
      check(helper, handler != null && !handler.insertItem(0, new ItemStack(Items.DIRT), true).isEmpty(),
        "A closed input must refuse items from that side");
      helper.succeed();
    });
  }
}
