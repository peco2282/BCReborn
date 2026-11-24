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
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.stripes.StripesPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The stripes pipe as in the original PipeItemsStripes: items reaching its open end go through the stripes handlers
 * (placing blocks, extending or retracting the pipe line), and engine power breaks the block in front.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class StripesGameTests {
  private static final BlockPos BACK = new BlockPos(0, 1, 1);
  private static final BlockPos STRIPES = new BlockPos(1, 1, 1);
  private static final BlockPos FRONT = new BlockPos(2, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static PipeBlockEntity place(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    helper.setBlock(pos, TransportBlocks.get(PipeType.ITEM, material).get().defaultBlockState());
    return (PipeBlockEntity) helper.getBlockEntity(pos);
  }

  /** A cobblestone pipe behind (west of) a stripes pipe facing east. */
  private static PipeBlockEntity line(GameTestHelper helper) {
    place(helper, BACK, PipeMaterial.COBBLESTONE);
    PipeBlockEntity stripes = place(helper, STRIPES, PipeMaterial.STRIPES);
    check(helper, StripesPipe.openOrientation(stripes) == Direction.EAST, "The stripes pipe must face east, faces " + StripesPipe.openOrientation(stripes));
    return stripes;
  }

  private static Item pipeItem(PipeMaterial material) {
    return TransportBlocks.get(PipeType.ITEM, material).get().asItem();
  }

  private static boolean isPipe(GameTestHelper helper, BlockPos pos, PipeMaterial material) {
    return helper.getBlockState(pos).getBlock() instanceof PipeBlock pipe && pipe.getPipeMaterial() == material
      && pipe.getTransportType() == PipeType.ITEM;
  }

  /** How many of {@code item} lie around or travel in the pipes of the test. */
  /** The GameTest world is reused: items dropped here by an earlier run would be counted by {@link #found}. */
  private static void clearOldDrops(GameTestHelper helper) {
    helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(STRIPES)).inflate(4)).forEach(ItemEntity::discard);
  }

  private static int found(GameTestHelper helper, Item item) {
    int count = 0;
    for (ItemEntity entity : helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(STRIPES)).inflate(4))) {
      if (entity.getItem().is(item)) count += entity.getItem().getCount();
    }
    for (int x = 0; x < 3; x++) {
      if (helper.getBlockEntity(new BlockPos(x, 1, 1)) instanceof PipeBlockEntity pipe) {
        for (TravelingItem travelling : pipe.getTravelingItems()) {
          if (travelling.getStack().is(item)) count += travelling.getStack().getCount();
        }
      }
    }
    return count;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesPlacesBlocks(GameTestHelper helper) {
    clearOldDrops(helper);
    PipeBlockEntity stripes = line(helper);
    stripes.injectItem(new ItemStack(Items.COBBLESTONE, 2), Direction.WEST);
    helper.succeedWhen(() -> {
      helper.assertBlockPresent(Blocks.COBBLESTONE, FRONT);
      helper.assertTrue(found(helper, Items.COBBLESTONE) == 1, "One cobblestone is placed and the other one is sent back");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesExtendsWithAnItemPipe(GameTestHelper helper) {
    clearOldDrops(helper);
    PipeBlockEntity stripes = line(helper);
    stripes.injectItem(new ItemStack(pipeItem(PipeMaterial.STONE)), Direction.WEST);
    helper.succeedWhen(() -> {
      helper.assertTrue(isPipe(helper, STRIPES, PipeMaterial.STONE), "The stone pipe must take the stripes pipe's place");
      helper.assertTrue(isPipe(helper, FRONT, PipeMaterial.STRIPES), "The stripes pipe must move one block forward");
      helper.assertTrue(found(helper, pipeItem(PipeMaterial.STONE)) == 0, "The stone pipe item must be used");
      helper.assertTrue(PipeBlock.isConnected(helper.getBlockState(FRONT), Direction.WEST),
        "The moved stripes pipe must connect to the new pipe");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesExtensionStopsAtABlock(GameTestHelper helper) {
    clearOldDrops(helper);
    PipeBlockEntity stripes = line(helper);
    helper.setBlock(FRONT, Blocks.STONE);
    stripes.injectItem(new ItemStack(pipeItem(PipeMaterial.STONE)), Direction.WEST);
    helper.runAfterDelay(30, () -> {
      check(helper, isPipe(helper, STRIPES, PipeMaterial.STRIPES), "A blocked stripes pipe must stay");
      check(helper, found(helper, pipeItem(PipeMaterial.STONE)) == 1, "The pipe item must come back, found " + found(helper, pipeItem(PipeMaterial.STONE)));
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesRetractsWithAVoidPipe(GameTestHelper helper) {
    clearOldDrops(helper);
    place(helper, BACK, PipeMaterial.COBBLESTONE);
    place(helper, STRIPES, PipeMaterial.STONE);
    PipeBlockEntity stripes = place(helper, FRONT, PipeMaterial.STRIPES);
    stripes.injectItem(new ItemStack(pipeItem(PipeMaterial.VOID)), Direction.WEST);
    helper.succeedWhen(() -> {
      helper.assertTrue(isPipe(helper, STRIPES, PipeMaterial.STRIPES), "The stripes pipe must move back onto the stone pipe");
      helper.assertBlockPresent(Blocks.AIR, FRONT);
      helper.assertTrue(found(helper, pipeItem(PipeMaterial.STONE)) == 1, "The removed stone pipe must come back");
      helper.assertTrue(found(helper, pipeItem(PipeMaterial.VOID)) == 1, "The void pipe must come back");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void poweredStripesBreaksTheBlockInFront(GameTestHelper helper) {
    clearOldDrops(helper);
    PipeBlockEntity stripes = line(helper);
    helper.setBlock(FRONT, Blocks.COBBLESTONE);
    var battery = stripes.getCapability(ForgeCapabilities.ENERGY).orElseThrow(IllegalStateException::new);
    check(helper, battery.getMaxEnergyStored() == StripesPipe.BATTERY && battery.receiveEnergy(100000, true) == StripesPipe.MAX_RECEIVE,
      "The stripes battery holds 16000 RF and takes 640 RF/t");
    battery.receiveEnergy(10, false);
    helper.succeedWhen(() -> {
      helper.assertBlockPresent(Blocks.AIR, FRONT);
      helper.assertTrue(found(helper, Items.COBBLESTONE) == 1, "The broken cobblestone must be sent back into the pipe");
      helper.assertTrue(stripes.getExtractionEnergy() == 0, "Breaking costs 10 RF");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesLeavesUnbreakableBlocks(GameTestHelper helper) {
    PipeBlockEntity stripes = line(helper);
    helper.setBlock(FRONT, Blocks.BEDROCK);
    stripes.getBattery().setEnergy(100);
    helper.runAfterDelay(20, () -> {
      check(helper, helper.getBlockState(FRONT).is(Blocks.BEDROCK), "Bedrock must not be broken");
      check(helper, stripes.getExtractionEnergy() == 100, "Nothing to break costs nothing, left " + stripes.getExtractionEnergy());
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void stripesWithoutPowerBreaksNothing(GameTestHelper helper) {
    line(helper);
    helper.setBlock(FRONT, Blocks.COBBLESTONE);
    helper.runAfterDelay(20, () -> {
      helper.assertBlockPresent(Blocks.COBBLESTONE, FRONT);
      helper.succeed();
    });
  }
}
