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
import com.peco2282.bcreborn.transport.pipe.extraction.StandardItemExtractionModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Wooden pipe family extraction as in the original PipeItemsWood / PipeItemsEmerald: the stored energy pays 10 RF per
 * item, a pull empties the battery, and the Emerald round robin takes one item of each filter in turn.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class WoodExtractionGameTests {
  private static final BlockPos CHEST = new BlockPos(0, 1, 1);
  private static final BlockPos PIPE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static ChestBlockEntity chest(GameTestHelper helper, ItemStack... stacks) {
    helper.setBlock(CHEST, Blocks.CHEST.defaultBlockState());
    ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(CHEST);
    for (int i = 0; i < stacks.length; i++) chest.setItem(i, stacks[i]);
    return chest;
  }

  private static PipeBlockEntity pipe(GameTestHelper helper, PipeMaterial material) {
    helper.setBlock(PIPE, TransportBlocks.get(PipeType.ITEM, material).get().defaultBlockState());
    helper.setBlock(PIPE.east(), TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(PIPE);
    check(helper, pipe.getExtractionSide() == Direction.WEST, "The pipe must extract from the chest, got " + pipe.getExtractionSide());
    return pipe;
  }

  private static int count(ChestBlockEntity chest, int slot) {
    return chest.getItem(slot).getCount();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodPipeTakesWhatItsEnergyPaysFor(GameTestHelper helper) {
    ChestBlockEntity chest = chest(helper, new ItemStack(Items.COBBLESTONE, 64));
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.WOOD);
    check(helper, pipe.getBattery().getMaxEnergyStored() == 2560 && pipe.getBattery().receiveEnergy(1000, true) == 80,
      "The wooden pipe battery holds 2560 RF and takes 80 RF/t");
    pipe.getBattery().setEnergy(55);
    helper.succeedWhen(() -> {
      helper.assertTrue(count(chest, 0) < 64, "The pipe must pull once 16 ticks have passed");
      helper.assertTrue(count(chest, 0) == 59, "55 RF pay for 5 items, took " + (64 - count(chest, 0)));
      helper.assertTrue(pipe.getExtractionEnergy() == 0, "A pull empties the battery, left " + pipe.getExtractionEnergy());
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodPipePullsEarlyWhenTheStackIsPaidFor(GameTestHelper helper) {
    ChestBlockEntity chest = chest(helper, new ItemStack(Items.COBBLESTONE, 3));
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.WOOD);
    pipe.resetTicksSincePull();
    pipe.getBattery().setEnergy(30);
    helper.onEachTick(() -> {
      if (!chest.getItem(0).isEmpty()) {
        check(helper, pipe.getTicksSincePull() < StandardItemExtractionModule.MAX_PULL_TICKS,
          "30 RF pay for the whole stack of 3, so the pull must come before 16 ticks");
      }
    });
    helper.succeedWhen(() -> helper.assertTrue(chest.getItem(0).isEmpty(), "The whole stack of 3 must be taken at once"));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodPipeWithoutEnergyTakesNothing(GameTestHelper helper) {
    ChestBlockEntity chest = chest(helper, new ItemStack(Items.COBBLESTONE, 64));
    pipe(helper, PipeMaterial.WOOD);
    helper.runAfterDelay(40, () -> {
      check(helper, count(chest, 0) == 64, "A wooden pipe without power must not extract");
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emeraldRoundRobinTakesOneOfEachFilter(GameTestHelper helper) {
    ChestBlockEntity chest = chest(helper, new ItemStack(Items.COBBLESTONE, 16), new ItemStack(Items.DIRT, 16));
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.EMERALD);
    pipe.getEmeraldFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    pipe.getEmeraldFilters().setItem(1, new ItemStack(Items.DIRT));
    pipe.setExtractFilterMode(PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN);
    helper.onEachTick(() -> {
      pipe.getBattery().setEnergy(2560);
      int cobble = 16 - count(chest, 0);
      int dirt = 16 - count(chest, 1);
      check(helper, Math.abs(cobble - dirt) <= 1, "Round robin alternates one item at a time, took " + cobble + " cobblestone and " + dirt + " dirt");
    });
    helper.succeedWhen(() -> {
      helper.assertTrue(count(chest, 0) <= 14 && count(chest, 1) <= 14, "Both filters must be served");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emeraldWhiteListTakesOnlyFilteredStacks(GameTestHelper helper) {
    ChestBlockEntity chest = chest(helper, new ItemStack(Items.DIRT, 16), new ItemStack(Items.COBBLESTONE, 16));
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.EMERALD);
    pipe.getEmeraldFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    pipe.setExtractFilterMode(PipeBlockEntity.ExtractFilterMode.WHITE_LIST);
    pipe.getBattery().setEnergy(2560);
    helper.onEachTick(() -> check(helper, count(chest, 0) == 16, "Dirt is not in the white list"));
    helper.succeedWhen(() -> helper.assertTrue(chest.getItem(1).isEmpty(),
      "2560 RF pay for the whole cobblestone stack, left " + chest.getItem(1)));
  }

  private static net.minecraftforge.common.util.FakePlayer player(GameTestHelper helper) {
    return net.minecraftforge.common.util.FakePlayerFactory.get(helper.getLevel(),
      new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "WoodTest"));
  }

  private static void use(GameTestHelper helper, PipeBlockEntity pipe, boolean wrench) {
    var player = player(helper);
    player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
      wrench ? new ItemStack(com.peco2282.bcreborn.core.CoreItems.WRENCH.get()) : ItemStack.EMPTY);
    var hit = new net.minecraft.world.phys.BlockHitResult(pipe.getBlockPos().getCenter(), Direction.UP, pipe.getBlockPos(), false);
    var state = pipe.getBlockState();
    state.getBlock().use(state, helper.getLevel(), pipe.getBlockPos(), player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodPipeTurnsOnlyWithTheWrench(GameTestHelper helper) {
    chest(helper);
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.WOOD);
    helper.setBlock(PIPE.east(), Blocks.AIR);
    helper.setBlock(PIPE.north(), Blocks.CHEST);
    check(helper, pipe.getExtractionSide() == Direction.WEST, "Another chest must not take the side over, got " + pipe.getExtractionSide());
    use(helper, pipe, false);
    check(helper, pipe.getExtractionSide() == Direction.WEST, "A bare hand must not turn the pipe");
    use(helper, pipe, true);
    check(helper, pipe.getExtractionSide() == Direction.NORTH, "The wrench turns to the next inventory, skipping empty sides, got " + pipe.getExtractionSide());
    use(helper, pipe, true);
    check(helper, pipe.getExtractionSide() == Direction.WEST, "And back to the first one, got " + pipe.getExtractionSide());
    check(helper, helper.getBlockState(PIPE).getValue(com.peco2282.bcreborn.transport.block.PipeBlock.EXTRACTION_SIDE) == Direction.WEST.get3DDataValue(),
      "The block state shows the side");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodPipeMovesOnWhenItsInventoryGoes(GameTestHelper helper) {
    chest(helper);
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.WOOD);
    helper.setBlock(PIPE.north(), Blocks.CHEST);
    helper.setBlock(CHEST, Blocks.AIR);
    check(helper, pipe.getExtractionSide() == Direction.NORTH, "Without its chest the pipe must face the other one, got " + pipe.getExtractionSide());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodenFluidPipeFacesATank(GameTestHelper helper) {
    helper.setBlock(PIPE.north(), com.peco2282.bcreborn.factory.FactoryBlocks.TANK.get());
    helper.setBlock(PIPE, TransportBlocks.get(PipeType.FLUID, PipeMaterial.WOOD).get());
    PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(PIPE);
    check(helper, pipe.getExtractionSide() == Direction.NORTH, "A wooden fluid pipe must face the tank, got " + pipe.getExtractionSide());
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void emeraldButtonsSetTheModeAndFiltersStay(GameTestHelper helper) {
    chest(helper);
    PipeBlockEntity pipe = pipe(helper, PipeMaterial.EMERALD);
    pipe.getEmeraldFilters().setItem(0, new ItemStack(Items.COBBLESTONE));
    var menu = new com.peco2282.bcreborn.transport.menu.EmeraldPipeMenu(1, player(helper).getInventory(), pipe);
    check(helper, menu.clickMenuButton(player(helper), PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN.ordinal())
      && pipe.getExtractFilterMode() == PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN, "The round robin button sets the mode");
    check(helper, menu.getMode() == PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN, "The menu shows the mode");
    helper.setBlock(PIPE.north(), Blocks.CHEST);
    use(helper, pipe, true);
    check(helper, pipe.getExtractionSide() == Direction.NORTH && pipe.getExtractFilterMode() == PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN,
      "The wrench turns the side, not the mode");
    check(helper, pipe.getEmeraldFilters().getItem(0).is(Items.COBBLESTONE), "The filters stay when the side turns");
    helper.succeed();
  }
}
