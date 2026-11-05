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
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.transport.ItemTransportModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the original 7.1.x {@code PipeTransportItems} jam handling: items with nowhere to go are dropped (only
 * Iron / Daizuli / Clay bounce), full inventories are not destinations, similar items are grouped, and an
 * overfilled pipe bursts.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class PipeJamGameTests {
  private static final BlockPos PIPE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity placePipe(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    BlockState state = TransportBlocks.get(PipeType.ITEM, material).get().defaultBlockState();
    helper.setBlock(pos, Block.updateFromNeighbourShapes(state, helper.getLevel(), helper.absolutePos(pos)));
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  private static int droppedCount(GameTestHelper helper, BlockPos pos, net.minecraft.world.item.Item item) {
    return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(pos)).inflate(3)).stream()
      .filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void deadEndDropsItemAtCentre(GameTestHelper helper) {
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeMaterial.COBBLESTONE);
    pipe.injectItemWithSpeed(new ItemStack(Items.FLINT, 3), Direction.WEST, 0.05f);
    check(helper, pipe.getTravelingItems().get(0).getNextDirection() == null,
      "A pipe with no other connection has no destination (original output UNKNOWN)");
    helper.succeedWhen(() -> {
      helper.assertTrue(pipe.getTravelingItems().isEmpty(), "The item must leave the pipe");
      helper.assertTrue(droppedCount(helper, PIPE, Items.FLINT) == 3, "The item must be dropped as an entity");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void fullInventoryIsNotADestination(GameTestHelper helper) {
    helper.setBlock(PIPE.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(PIPE.south(), Blocks.CHEST.defaultBlockState());
    ChestBlockEntity full = (ChestBlockEntity) helper.getBlockEntity(PIPE.north());
    for (int i = 0; i < full.getContainerSize(); i++) {
      full.setItem(i, new ItemStack(Items.STONE, 64));
    }
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeMaterial.COBBLESTONE);
    for (int i = 0; i < 5; i++) {
      pipe.injectItemWithSpeed(new ItemStack(Items.FLINT), Direction.WEST, 0.05f);
    }
    for (TravelingItem item : pipe.getTravelingItems()) {
      check(helper, item.getNextDirection() == Direction.SOUTH,
        "A full chest must not be chosen (original canReceivePipeObjects), got " + item.getNextDirection());
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPipeBouncesInsteadOfDropping(GameTestHelper helper) {
    helper.setBlock(PIPE.west(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(PIPE.east(), Blocks.CHEST.defaultBlockState());
    ChestBlockEntity full = (ChestBlockEntity) helper.getBlockEntity(PIPE.east());
    for (int i = 0; i < full.getContainerSize(); i++) {
      full.setItem(i, new ItemStack(Items.STONE, 64));
    }
    // A clay pipe (allowBouncing) whose only other side is full sends the item back.
    PipeBlockEntity clay = placePipe(helper, PIPE, PipeMaterial.CLAY);
    clay.injectItemWithSpeed(new ItemStack(Items.FLINT), Direction.WEST, 0.05f);
    check(helper, clay.getTravelingItems().get(0).getNextDirection() == Direction.WEST,
      "A bouncing pipe must send the item back when nothing else takes it, got " + clay.getTravelingItems().get(0).getNextDirection());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void similarItemsAreGrouped(GameTestHelper helper) {
    helper.setBlock(PIPE.east(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeMaterial.COBBLESTONE);
    for (int i = 0; i < 40; i++) {
      pipe.injectItemWithSpeed(new ItemStack(Items.COBBLESTONE), Direction.WEST, 0.01f);
    }
    int stacks = pipe.getTravelingItems().size();
    int total = pipe.getTravelingItems().stream().mapToInt(t -> t.getStack().getCount()).sum();
    check(helper, stacks < ItemTransportModule.MAX_PIPE_STACKS / 2,
      "From " + ItemTransportModule.MAX_PIPE_STACKS / 2 + " stacks on, similar items must be merged, got " + stacks);
    check(helper, total == 40, "Grouping must not lose items, got " + total);
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void overfilledPipeBursts(GameTestHelper helper) {
    helper.setBlock(PIPE.east(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PIPE, PipeMaterial.COBBLESTONE);
    // Swords do not stack, so they cannot be grouped.
    for (int i = 0; i <= ItemTransportModule.MAX_PIPE_STACKS; i++) {
      pipe.injectItemWithSpeed(new ItemStack(Items.IRON_SWORD), Direction.WEST, 0.01f);
    }
    check(helper, helper.getBlockState(PIPE).isAir(), "More than " + ItemTransportModule.MAX_PIPE_STACKS + " stacks must burst the pipe");
    int swords = droppedCount(helper, PIPE, Items.IRON_SWORD);
    check(helper, swords == ItemTransportModule.MAX_PIPE_STACKS + 1, "The burst pipe must drop its contents, got " + swords);
    helper.succeed();
  }
}
