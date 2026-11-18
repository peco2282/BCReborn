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
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import com.peco2282.bcreborn.factory.block.entity.TankBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.FilteredBufferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/** Comparator output of the blocks that had one in the original (BlockTank, IComparatorInventory blocks). */
@GameTestHolder(BCRebornTransport.MODID)
public class ComparatorGameTests {
  private static final BlockPos POS = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static int signal(GameTestHelper helper, BlockPos pos) {
    var state = helper.getBlockState(pos);
    check(helper, state.hasAnalogOutputSignal(), state.getBlock() + " must give a comparator signal");
    return state.getAnalogOutputSignal(helper.getLevel(), helper.absolutePos(pos));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void tankSignalFollowsTheWholeColumn(GameTestHelper helper) {
    helper.setBlock(POS, FactoryBlocks.TANK.get());
    helper.setBlock(POS.above(), FactoryBlocks.TANK.get());
    TankBlockEntity bottom = (TankBlockEntity) helper.getBlockEntity(POS);
    TankBlockEntity top = (TankBlockEntity) helper.getBlockEntity(POS.above());
    check(helper, signal(helper, POS) == 0, "An empty tank gives no signal");
    int capacity = bottom.getTankCapacity(0);
    top.fill(new FluidStack(Fluids.WATER, capacity), FluidAction.EXECUTE);
    check(helper, signal(helper, POS) == 7 && signal(helper, POS.above()) == 7,
      "One full tank of two gives 15 / 2 on both tanks, got " + signal(helper, POS) + " / " + signal(helper, POS.above()));
    bottom.fill(new FluidStack(Fluids.WATER, capacity), FluidAction.EXECUTE);
    check(helper, signal(helper, POS) == 15, "A full column gives 15, got " + signal(helper, POS));
    top.drain(capacity * 2, FluidAction.EXECUTE);
    check(helper, signal(helper, POS) == 0, "Draining it gives 0 again, got " + signal(helper, POS));
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void hopperCountsEverySlot(GameTestHelper helper) {
    helper.setBlock(POS, FactoryBlocks.HOPPER.get());
    HopperBlockEntity hopper = (HopperBlockEntity) helper.getBlockEntity(POS);
    check(helper, signal(helper, POS) == 0, "An empty hopper gives no signal");
    hopper.setItem(0, new ItemStack(Items.COBBLESTONE, 1));
    check(helper, signal(helper, POS) == 1, "Any item gives at least 1, got " + signal(helper, POS));
    for (int i = 0; i < hopper.getContainerSize(); i++) hopper.setItem(i, new ItemStack(Items.COBBLESTONE, 64));
    check(helper, signal(helper, POS) == 15, "A full hopper gives 15, got " + signal(helper, POS));
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void requesterCountsRequestedSlots(GameTestHelper helper) {
    helper.setBlock(POS, RoboticsBlocks.REQUESTER.get());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(POS);
    check(helper, signal(helper, POS) == 0, "Without requests there is no signal");
    requester.setRequest(0, new ItemStack(Items.COBBLESTONE, 64));
    requester.setItem(0, new ItemStack(Items.COBBLESTONE, 64));
    check(helper, signal(helper, POS) == 15, "The only requested slot is full, got " + signal(helper, POS));
    requester.setRequest(1, new ItemStack(Items.DIRT, 64));
    check(helper, signal(helper, POS) == 8, "Half of the requested slots are full, got " + signal(helper, POS));
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void filteredBufferCountsFilteredSlots(GameTestHelper helper) {
    helper.setBlock(POS, TransportBlocks.FILTERED_BUFFER.get());
    FilteredBufferBlockEntity buffer = (FilteredBufferBlockEntity) helper.getBlockEntity(POS);
    buffer.getFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    buffer.setItem(0, new ItemStack(Items.COBBLESTONE, 64));
    check(helper, signal(helper, POS) == 15, "The only filtered slot is full, got " + signal(helper, POS));
    helper.succeed();
  }
}
