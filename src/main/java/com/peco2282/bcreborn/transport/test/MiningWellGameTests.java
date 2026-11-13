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
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the Mining Well against the original 7.1.x {@code TileMiningWell} / {@code BlockMiningWell}.
 * <p>
 * Column below WELL: two stone blocks, one air block, then bedrock that stops the well.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class MiningWellGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos WELL = new BlockPos(6, 6, 6);
  private static final BlockPos CHEST = new BlockPos(7, 6, 6);

  private static MiningWellBlockEntity placeWell(GameTestHelper helper) {
    helper.setBlock(WELL.below(1), Blocks.STONE.defaultBlockState());
    helper.setBlock(WELL.below(2), Blocks.STONE.defaultBlockState());
    helper.setBlock(WELL.below(3), Blocks.AIR.defaultBlockState());
    helper.setBlock(WELL.below(4), Blocks.BEDROCK.defaultBlockState());
    helper.setBlock(CHEST, Blocks.CHEST.defaultBlockState());
    helper.setBlock(WELL, FactoryBlocks.MINING_WELL.get().defaultBlockState());
    return (MiningWellBlockEntity) helper.getBlockEntity(WELL);
  }

  private static int count(Container container) {
    int n = 0;
    for (int i = 0; i < container.getContainerSize(); i++) {
      if (container.getItem(i).is(Items.COBBLESTONE)) {
        n += container.getItem(i).getCount();
      }
    }
    return n;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void miningWellDigsDownAndStopsAtBedrock(GameTestHelper helper) {
    MiningWellBlockEntity well = placeWell(helper);
    well.getBattery().setEnergy(well.getBattery().getMaxEnergyStored());

    boolean[] removed = {false};
    helper.succeedWhen(() -> {
      if (!removed[0]) {
        for (int d = 1; d <= 3; d++) {
          helper.assertBlockPresent(FactoryBlocks.PLAIN_PIPE.get(), WELL.below(d));
        }
        helper.assertBlockPresent(Blocks.BEDROCK, WELL.below(4));
        helper.assertTrue(count((Container) helper.getBlockEntity(CHEST)) == 2,
          "Both mined blocks must go to the adjacent chest");
        helper.assertTrue(!well.hasWork(), "The well must report no work once it reaches bedrock");
        // Original BlockMiningWell.breakBlock(): removing the well removes its pipes.
        helper.setBlock(WELL, Blocks.AIR.defaultBlockState());
        removed[0] = true;
      }
      for (int d = 1; d <= 3; d++) {
        helper.assertBlockPresent(Blocks.AIR, WELL.below(d));
      }
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void miningWellNeedsEnergyAndObeysOff(GameTestHelper helper) {
    MiningWellBlockEntity well = placeWell(helper);

    helper.runAfterDelay(20, () -> {
      helper.assertBlockPresent(Blocks.STONE, WELL.below(1));
      well.setControlMode(IControllable.Mode.Off);
      well.getBattery().setEnergy(well.getBattery().getMaxEnergyStored());
    });
    helper.runAfterDelay(60, () -> {
      helper.assertBlockPresent(Blocks.STONE, WELL.below(1));
      if (well.hasWork()) {
        helper.fail("A well switched off must report no work");
      }
      if (well.acceptsControlMode(IControllable.Mode.Loop)) {
        helper.fail("The well only accepts Off and On");
      }
      if (!BuiltInLootTables.EMPTY.equals(FactoryBlocks.PLAIN_PIPE.get().getLootTable())) {
        helper.fail("Plain pipes must not drop anything");
      }
      helper.succeed();
    });
  }
}
