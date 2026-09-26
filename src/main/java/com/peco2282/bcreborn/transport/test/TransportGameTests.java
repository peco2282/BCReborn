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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.items.IItemHandler;

@GameTestHolder(BCRebornTransport.MODID)
public class TransportGameTests {
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void testItemRouteAvailableBeforeCenter(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());
    helper.setBlock(pos.north(), Blocks.CHEST.defaultBlockState());
    helper.runAtTickTime(10, () -> {
      PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(pos);
      pipe.injectItem(new ItemStack(Items.DIAMOND), Direction.WEST);
      TravelingItem item = pipe.getTravelingItems().get(0);
      if (item.getProgress() != 0 || item.getNextDirection() != Direction.NORTH) {
        helper.fail("The west-to-north route must be available immediately on injection");
      }
      TravelingItem synced = TravelingItem.load(item.save());
      if (synced.getEntryDirection() != Direction.WEST || synced.getNextDirection() != Direction.NORTH) {
        helper.fail("The route must survive serialization for client rendering");
      }
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void testItemReverseAtBlockedExit(GameTestHelper helper) {
    for (Direction entry : Direction.values()) {
      for (Direction exit : Direction.values()) {
        TravelingItem item = new TravelingItem(new ItemStack(Items.DIAMOND), entry);
        item.setNextDirection(exit);
        item.setProgress(1);
        item.setCenterReached(true);
        item.reverse();
        TravelingItem synced = TravelingItem.load(item.save());
        if (synced.getEntryDirection() != exit || synced.getNextDirection() != entry
          || synced.getProgress() != 0 || synced.getPrevProgress() != 0
          || item.isCenterReached() || synced.getBounceCount() != 1) {
          helper.fail("A reversed item must start at its blocked exit and return to its original entrance");
        }
      }
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void testItemTransport(GameTestHelper helper) {
    BlockPos pos1 = new BlockPos(0, 1, 1);
    BlockPos pos2 = new BlockPos(1, 1, 1);

    // パイプを設置
    helper.setBlock(pos1, TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());
    helper.setBlock(pos2, TransportBlocks.get(PipeType.ITEM, PipeMaterial.COBBLESTONE).get().defaultBlockState());

    // チェストを設置（目的地）
    BlockPos chestPos = new BlockPos(2, 1, 1);
    helper.setBlock(chestPos, Blocks.CHEST.defaultBlockState());

    helper.runAtTickTime(10, () -> {
      BlockEntity be = helper.getBlockEntity(pos1);
      if (be instanceof PipeBlockEntity pipeBE) {
        pipeBE.injectItem(new ItemStack(Items.DIAMOND), Direction.WEST);
      }
    });

    helper.succeedWhen(() -> {
      BlockEntity be = helper.getBlockEntity(chestPos);
      if (be == null) helper.fail("Chest not found", chestPos);
      //noinspection DataFlowIssue
      IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
      //noinspection ConstantValue
      if (handler == null) helper.fail("Item handler not found");

      boolean found = false;
      for (int i = 0; i < handler.getSlots(); i++) {
        if (handler.getStackInSlot(i).is(Items.DIAMOND)) {
          found = true;
          break;
        }
      }
      if (!found) helper.fail("Diamond not found in chest");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void testEnergyTransport(GameTestHelper helper) {
    // 構成: Creativeエンジン(0,1,1) -> 木エネルギーパイプ(1,1,1) -> 金エネルギーパイプ(2,1,1)
    // 東向きのエンジンとFE配管。木エンジンは汎用FE供給には使えない。

    BlockPos enginePos = new BlockPos(0, 1, 1);
    BlockPos woodPipePos = new BlockPos(1, 1, 1);
    BlockPos goldPipePos = new BlockPos(2, 1, 1);
    BlockPos minerPos = new BlockPos(3, 1, 1);

    // Creativeエンジンを設置
    helper.setBlock(enginePos, com.peco2282.bcreborn.energy.EnergyBlocks.CREATIVE_ENGINE.get().defaultBlockState()
      .setValue(com.peco2282.bcreborn.common.block.EngineBlock.FACING, Direction.EAST));

    // パイプを設置
    helper.setBlock(woodPipePos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    helper.setBlock(goldPipePos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.GOLD).get().defaultBlockState());

    // Minerを設置
    helper.setBlock(minerPos, com.peco2282.bcreborn.factory.FactoryBlocks.MINING_WELL.get().defaultBlockState());

    // エンジンにレッドストーン信号を与える
    helper.setBlock(enginePos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());

    helper.runAtTickTime(100, () -> {
      // 配管を通ってエネルギーが到達するのを待つ
      // Minerにエネルギーが届いているか確認
      BlockEntity be = helper.getBlockEntity(minerPos);
      if (be instanceof com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity minerBE) {
        int stored = minerBE.getBattery().getEnergyStored();
        if (stored <= 0) {
          // まだ届いていない可能性もあるので、succeedWhen で継続チェック
        }
      }
    });

    helper.succeedWhen(() -> {
      BlockEntity be = helper.getBlockEntity(minerPos);
      if (!(be instanceof com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity))
        helper.fail("Miner not found");
      com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity minerBE = (com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity) be;
      int stored = minerBE.getBattery().getEnergyStored();
      // 100tick後にはエネルギーが蓄積されているはず
      if (stored <= 0) helper.fail("Energy not reaching miner. Current stored: " + stored);
    });
  }
}
