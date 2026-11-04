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
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.menu.DiamondPipeMenu;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.impl.IronPipeLogic;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies behaviour matching the original 7.1.x PipeItemsIron / PipeLogicIron / PipeItemsDiamond / ContainerDiamondPipe.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class IronDiamondPipeGameTests {
  private static final BlockPos CENTER = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /** Places a pipe with connections computed from its neighbours (same appearance as a player placement). */
  private static PipeBlockEntity placePipe(GameTestHelper helper, PipeType type, PipeMaterial material) {
    BlockPos absolute = helper.absolutePos(CENTER);
    BlockState state = TransportBlocks.get(type, material).get().defaultBlockState();
    state = Block.updateFromNeighbourShapes(state, helper.getLevel(), absolute);
    helper.setBlock(CENTER, state);
    return (PipeBlockEntity) helper.getBlockEntity(CENTER);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPipeChoosesValidOutputOnPlaceAndRedstone(GameTestHelper helper) {
    helper.setBlock(CENTER.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(CENTER.east(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PipeType.ITEM, PipeMaterial.IRON);

    // Original onBlockPlaced: switchPosition from UP -> next valid side is NORTH
    check(helper, pipe.getIronPipeOutput() == Direction.NORTH,
      "Iron pipe must pick the first valid output after UP on placement, got " + pipe.getIronPipeOutput());

    // Redstone rising edge switches to the next valid side (EAST)
    helper.setBlock(CENTER.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    check(helper, pipe.getIronPipeOutput() == Direction.EAST,
      "Redstone edge must switch iron pipe output to the next valid side, got " + pipe.getIronPipeOutput());

    // The falling edge switches too (EAST -> NORTH)
    helper.setBlock(CENTER.below(), Blocks.AIR.defaultBlockState());
    check(helper, pipe.getIronPipeOutput() == Direction.NORTH,
      "Redstone falling edge must also switch iron pipe output, got " + pipe.getIronPipeOutput());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPipeConnectsToAllInventoriesButOutputsOnlyOne(GameTestHelper helper) {
    helper.setBlock(CENTER.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(CENTER.south(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PipeType.ITEM, PipeMaterial.IRON);

    check(helper, pipe.getBlockState().getValue(com.peco2282.bcreborn.transport.block.PipeBlock.SOUTH)
        && pipe.getBlockState().getValue(com.peco2282.bcreborn.transport.block.PipeBlock.NORTH),
      "Iron pipe must connect to inventories on every side like the original");
    check(helper, IronPipeLogic.setFacing(pipe, Direction.SOUTH), "SOUTH must be a valid iron output");
    check(helper, !IronPipeLogic.setFacing(pipe, Direction.WEST), "Unconnected side must be rejected");

    pipe.injectItem(new ItemStack(Items.COBBLESTONE), Direction.WEST);
    TravelingItem item = pipe.getTravelingItems().get(0);
    check(helper, item.getNextDirection() == Direction.SOUTH,
      "Iron pipe must force items to its output side, got " + item.getNextDirection());

    pipe.injectItem(new ItemStack(Items.COBBLESTONE), Direction.SOUTH);
    TravelingItem fromOutput = pipe.getTravelingItems().get(1);
    check(helper, fromOutput.getNextDirection() == Direction.SOUTH,
      "Items entering from the output side must bounce back, got " + fromOutput.getNextDirection());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void ironPipeWrenchArmAndCenterHitResolution(GameTestHelper helper) {
    BlockPos absolute = helper.absolutePos(CENTER);
    Vec3 base = Vec3.atLowerCornerOf(absolute);
    BlockHitResult east = new BlockHitResult(base.add(0.95, 0.5, 0.5), Direction.EAST, absolute, false);
    BlockHitResult armTop = new BlockHitResult(base.add(0.5, 0.75, 0.05), Direction.UP, absolute, false);
    BlockHitResult center = new BlockHitResult(base.add(0.5, 0.75, 0.5), Direction.UP, absolute, false);
    check(helper, IronPipeLogic.resolveHitArm(absolute, east) == Direction.EAST, "East arm must resolve to EAST");
    check(helper, IronPipeLogic.resolveHitArm(absolute, armTop) == Direction.NORTH,
      "Top face of the north arm must resolve to NORTH, not the clicked face");
    check(helper, IronPipeLogic.resolveHitArm(absolute, center) == null, "Core hit must resolve to the centre");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void diamondMenuEditsFiltersAndRoutes(GameTestHelper helper) {
    helper.setBlock(CENTER.north(), Blocks.CHEST.defaultBlockState());
    helper.setBlock(CENTER.south(), Blocks.CHEST.defaultBlockState());
    PipeBlockEntity pipe = placePipe(helper, PipeType.ITEM, PipeMaterial.DIAMOND);

    var player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "DiamondPipeMenuTest"));
    BlockPos absolute = helper.absolutePos(CENTER);
    player.setPos(absolute.getX() + 0.5, absolute.getY() + 0.5, absolute.getZ() + 0.5);

    // The server-side constructor (no buf) must still create 54 + 36 slots
    DiamondPipeMenu menu = new DiamondPipeMenu(1, player.getInventory(), pipe);
    check(helper, menu.slots.size() == 54 + 36, "Diamond menu must expose 54 filter + 36 player slots");
    check(helper, menu.stillValid(player), "Diamond menu must be valid next to the pipe");

    // Only a single ghost item is placed at the start of the NORTH row (y = 2)
    int northSlot = Direction.NORTH.get3DDataValue() * 9;
    menu.setCarried(new ItemStack(Items.DIAMOND, 12));
    menu.clicked(northSlot, 0, ClickType.PICKUP, player);
    ItemStack ghost = pipe.getFilter(Direction.NORTH).getItem(0);
    check(helper, ghost.is(Items.DIAMOND) && ghost.getCount() == 1 && menu.getCarried().getCount() == 12,
      "Filter slot must hold a single ghost item without consuming the carried stack");

    // Matching item -> NORTH; non-matching item -> unfiltered SOUTH
    pipe.injectItem(new ItemStack(Items.DIAMOND), Direction.WEST);
    pipe.injectItem(new ItemStack(Items.DIRT), Direction.WEST);
    check(helper, pipe.getTravelingItems().get(0).getNextDirection() == Direction.NORTH,
      "Matching item must follow the diamond filter");
    check(helper, pipe.getTravelingItems().get(1).getNextDirection() == Direction.SOUTH,
      "Unfiltered item must avoid sides that have filters");

    // Filters must survive an NBT round trip
    CompoundTag tag = pipe.saveWithoutMetadata();
    PipeBlockEntity restored = new PipeBlockEntity(pipe.getBlockPos(), pipe.getBlockState(), PipeType.ITEM, PipeMaterial.DIAMOND);
    restored.load(tag);
    check(helper, restored.getFilter(Direction.NORTH).getItem(0).is(Items.DIAMOND),
      "Diamond filters must survive save/load");
    helper.succeed();
  }
}
