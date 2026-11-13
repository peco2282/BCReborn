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
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import com.peco2282.bcreborn.api.RegistryUtil;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.block.entity.ChargingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the Charging Table against the original 7.1.x {@code TileChargingTable}, with the robot item's
 * energy (original {@code IEnergyContainerItem}) exposed as the Forge energy capability.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class ChargingTableGameTests {
  private static final BlockPos TABLE = new BlockPos(1, 2, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static void tick(GameTestHelper helper, BuildCraftBlockEntity entity) {
    BuildCraftBlockEntity.<BuildCraftBlockEntity>ticker().tick(helper.getLevel(), entity.getBlockPos(), entity.getBlockState(), entity);
  }

  private static ItemStack programmedRobot(GameTestHelper helper, int energy) {
    for (RedstoneBoardNBT<?> board : RegistryUtil.getRedstoneBoardsList()) {
      if (board instanceof RedstoneBoardRobotNBT robot && board != RoboticsRedstoneRobots.EMPTY.get()) {
        return RobotItem.createRobotStack(robot, energy);
      }
    }
    helper.fail("No programmed robot board is registered");
    return ItemStack.EMPTY;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void chargingTableChargesRobot(GameTestHelper helper) {
    helper.setBlock(TABLE, SiliconBlocks.CHARGING_TABLE.get().defaultBlockState());
    ChargingTableBlockEntity table = (ChargingTableBlockEntity) helper.getBlockEntity(TABLE);
    check(helper, !table.hasWork() && !table.requiresLaserEnergy(), "An empty table has no work");

    ItemStack robot = programmedRobot(helper, RobotEntityBase.MAX_ENERGY - 3000);
    check(helper, table.canPlaceItem(0, robot) && !table.canPlaceItem(0, new ItemStack(Items.DIRT)),
      "Only items that hold energy are accepted");
    table.setItem(0, robot);
    check(helper, table.hasWork() && table.getRequiredEnergy() == 3000 && table.requiresLaserEnergy(),
      "The table asks for the missing energy, got " + table.getRequiredEnergy());

    table.receiveLaserEnergy(1000);
    tick(helper, table);
    check(helper, RobotItem.getEnergy(table.getItem(0)) == RobotEntityBase.MAX_ENERGY - 2000 && table.getEnergy() == 0,
      "Laser energy moves into the robot");

    table.receiveLaserEnergy(2500);
    tick(helper, table);
    check(helper, RobotItem.getEnergy(table.getItem(0)) == RobotEntityBase.MAX_ENERGY && table.getEnergy() == 500,
      "A full robot takes no more than its capacity");
    check(helper, !table.hasWork(), "A full robot leaves no work");

    // Original: with nothing to charge, leftover energy drains by 10 per tick.
    tick(helper, table);
    check(helper, table.getEnergy() == 490, "Leftover energy drains, got " + table.getEnergy());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void robotWithoutBoardHoldsNoEnergy(GameTestHelper helper) {
    ItemStack empty = RobotItem.createRobotStack(RoboticsRedstoneRobots.EMPTY.get(), 0);
    var storage = empty.getCapability(ForgeCapabilities.ENERGY).orElse(null);
    check(helper, storage != null, "The robot item exposes energy");
    check(helper, storage.getMaxEnergyStored() == 0 && storage.receiveEnergy(1000, false) == 0
      && RobotItem.getEnergy(empty) == 0, "A robot without a board cannot be charged");

    ItemStack robot = programmedRobot(helper, 500);
    var charged = robot.getCapability(ForgeCapabilities.ENERGY).orElse(null);
    check(helper, charged.getEnergyStored() == 500 && charged.getMaxEnergyStored() == RobotEntityBase.MAX_ENERGY,
      "A programmed robot reports its energy");
    check(helper, charged.extractEnergy(800, false) == 500 && RobotItem.getEnergy(robot) == 0,
      "Extraction is limited to the stored energy");
    helper.succeed();
  }
}
