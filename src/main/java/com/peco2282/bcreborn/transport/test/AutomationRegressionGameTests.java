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
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.energy.EnergyBlocks;
import com.peco2282.bcreborn.energy.block.entity.StoneEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

/**
 * Regressions found while running a quarry: machine output into double chests (original
 * {@code InvUtils.getInventory}) and fuel insertion into the Stone Engine (original
 * {@code TileEngineWithInventory}).
 */
@GameTestHolder(BCRebornTransport.MODID)
public class AutomationRegressionGameTests {
  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static int count(Container container) {
    int n = 0;
    for (int i = 0; i < container.getContainerSize(); i++) {
      n += container.getItem(i).getCount();
    }
    return n;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void machineOutputFillsBothHalvesOfDoubleChest(GameTestHelper helper) {
    BlockPos left = new BlockPos(0, 2, 1);
    BlockPos right = new BlockPos(1, 2, 1);
    // Facing NORTH, a LEFT half connects to the chest east of it and a RIGHT half to the one west of it.
    helper.setBlock(left, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH)
      .setValue(ChestBlock.TYPE, ChestType.LEFT));
    helper.setBlock(right, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH)
      .setValue(ChestBlock.TYPE, ChestType.RIGHT));
    ChestBlockEntity a = (ChestBlockEntity) helper.getBlockEntity(left);
    ChestBlockEntity b = (ChestBlockEntity) helper.getBlockEntity(right);
    check(helper, InvUtils.getInventory(a).getContainerSize() == 54 && InvUtils.getInventory(b).getContainerSize() == 54,
      "A double chest is one inventory of 54 slots from either half");

    // A machine above the left half (as a quarry next to a large chest) outputs 40 stacks.
    BlockPos machine = helper.absolutePos(left.above());
    int accepted = 0;
    for (int i = 0; i < 40; i++) {
      accepted += Utils.addToRandomInventoryAround(helper.getLevel(), machine, new ItemStack(Items.COBBLESTONE, 64));
    }
    check(helper, accepted == 40 * 64, "All 40 stacks must fit into the double chest, accepted " + accepted);
    check(helper, count(a) > 0 && count(b) > 0 && count(a) + count(b) == 40 * 64,
      "Both halves must be used, got " + count(a) + " and " + count(b));

    helper.setBlock(right, Blocks.AIR.defaultBlockState());
    helper.setBlock(left, Blocks.CHEST.defaultBlockState());
    check(helper, InvUtils.getInventory((Container) helper.getBlockEntity(left)).getContainerSize() == 27,
      "A single chest stays a single inventory");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void stoneEngineTakesFuelFromHopper(GameTestHelper helper) {
    BlockPos enginePos = new BlockPos(1, 2, 1);
    helper.setBlock(enginePos, EnergyBlocks.STONE_ENGINE.get().defaultBlockState()
      .setValue(EngineBlock.FACING, Direction.EAST));
    StoneEngineBlockEntity engine = (StoneEngineBlockEntity) helper.getBlockEntity(enginePos);

    check(helper, engine.getSlotsForFace(Direction.EAST).length == 0, "Nothing goes through the output side");
    check(helper, engine.getSlotsForFace(Direction.UP).length == 1 && engine.getSlotsForFace(Direction.WEST).length == 1,
      "Every other side reaches the fuel slot");
    check(helper, engine.canPlaceItem(0, new ItemStack(Items.LAVA_BUCKET)) && engine.canPlaceItem(0, new ItemStack(Items.COAL))
      && !engine.canPlaceItem(0, new ItemStack(Items.COBBLESTONE)), "Only fuel is accepted");

    // Pipes use the item handler capability.
    IItemHandler handler = engine.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.WEST).orElse(null);
    check(helper, handler != null && handler.insertItem(0, new ItemStack(Items.COBBLESTONE), true).getCount() == 1
      && handler.insertItem(0, new ItemStack(Items.COAL), true).isEmpty(), "The item handler follows the same rules");
    check(helper, !engine.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.EAST)
      .map(h -> h.getSlots() > 0).orElse(false), "The output side has no slots");

    // A hopper on top pushes a lava bucket into the engine.
    helper.setBlock(enginePos.above(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
    ((Container) helper.getBlockEntity(enginePos.above())).setItem(0, new ItemStack(Items.LAVA_BUCKET));

    boolean[] powered = {false};
    helper.succeedWhen(() -> {
      if (!powered[0]) {
        helper.assertTrue(engine.getItem(0).is(Items.LAVA_BUCKET), "The hopper must insert the lava bucket");
        // Original: burning a lava bucket leaves the empty bucket in the slot.
        helper.setBlock(enginePos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
        powered[0] = true;
      }
      helper.assertTrue(engine.isBurning() && engine.getItem(0).is(Items.BUCKET),
        "The engine must burn the lava and keep the bucket, slot holds " + engine.getItem(0));
    });
  }
}
