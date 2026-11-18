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
import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.RefineryBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The Sandstone and Emerald power pipes (original PipePowerSandstone / PipePowerEmerald): Sandstone carries power
 * between pipes only, Emerald takes power from engines like the wooden power pipe.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class PowerPipeGameTests {
  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static void engine(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.setBlock(pos, EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.EAST));
  }

  private static void pipe(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    helper.setBlock(pos, TransportBlocks.get(PipeType.ENERGY, material).get().defaultBlockState());
  }

  /** A machine that only stores power (a mining well would dig out of the test area). */
  private static RefineryBlockEntity machine(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos, FactoryBlocks.REFINERY.get().defaultBlockState());
    return (RefineryBlockEntity) helper.getBlockEntity(pos);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emeraldPowerPipeExtractsFromEngine(GameTestHelper helper) {
    check(helper, PipeMaterial.EMERALD.getEnergyTransferRate() == 2560 && PipeMaterial.SANDSTONE.getEnergyTransferRate() == 320,
      "Emerald carries 32 and Sandstone 4 times the cobblestone power pipe");
    engine(helper, new BlockPos(0, 1, 1));
    pipe(helper, new BlockPos(1, 1, 1), PipeMaterial.EMERALD);
    pipe(helper, new BlockPos(2, 1, 1), PipeMaterial.GOLD);
    RefineryBlockEntity machine = machine(helper, new BlockPos(2, 1, 2));
    helper.succeedWhen(() -> helper.assertTrue(machine.getEnergyStored() > 0,
      "An emerald power pipe must pass engine power on, the machine has " + machine.getEnergyStored()));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void sandstonePowerPipeSkipsMachines(GameTestHelper helper) {
    // Engine -> wood -> sandstone -> gold (above) -> far machine; a second machine sits right beside the sandstone pipe.
    engine(helper, new BlockPos(0, 1, 1));
    pipe(helper, new BlockPos(1, 1, 1), PipeMaterial.WOOD);
    BlockPos sandstonePos = new BlockPos(2, 1, 1);
    pipe(helper, sandstonePos, PipeMaterial.SANDSTONE);
    pipe(helper, new BlockPos(2, 2, 1), PipeMaterial.GOLD);
    RefineryBlockEntity beside = machine(helper, new BlockPos(2, 1, 2));
    RefineryBlockEntity far = machine(helper, new BlockPos(2, 2, 0));
    PipeBlockEntity sandstone = (PipeBlockEntity) helper.getBlockEntity(sandstonePos);
    check(helper, !sandstone.canTransferEnergy(Direction.SOUTH) && sandstone.canTransferEnergy(Direction.WEST)
        && sandstone.canTransferEnergy(Direction.UP),
      "A sandstone power pipe joins pipes but not machines");
    check(helper, !sandstone.getCapability(ForgeCapabilities.ENERGY, Direction.SOUTH).isPresent(),
      "A sandstone power pipe offers no energy to a machine");
    helper.onEachTick(() -> {
      check(helper, !helper.getBlockState(sandstonePos).getValue(PipeBlock.PROPERTY_MAP.get(Direction.SOUTH)),
        "The sandstone pipe must not show an arm towards the machine");
      check(helper, beside.getEnergyStored() == 0,
        "The machine beside the sandstone pipe must get nothing, has " + beside.getEnergyStored());
    });
    helper.succeedWhen(() -> helper.assertTrue(far.getEnergyStored() > 0,
      "Power must cross the sandstone pipe to the gold pipe's machine, has " + far.getEnergyStored()));
  }
}
