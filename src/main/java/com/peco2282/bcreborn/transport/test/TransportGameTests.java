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
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
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

  // Energy tests are temporarily disabled due to DEDICATED_SERVER environment issues in GameTestServer
  // @PrefixGameTestTemplate(false)
  // @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void testEnergyTransport(GameTestHelper helper) {
    // 構成: 木エンジン(0,1,1) -> 木エネルギーパイプ(1,1,1) -> 金エネルギーパイプ(2,1,1)
    // 木エンジンは北向き(Direction.NORTH, 負のZ方向)に設置するが、テンプレート内なので相対座標で調整
    // ここでは簡易的に東向き(Direction.EAST)にエンジンを置き、東隣にパイプを並べる

    BlockPos enginePos = new BlockPos(0, 1, 1);
    BlockPos woodPipePos = new BlockPos(1, 1, 1);
    BlockPos goldPipePos = new BlockPos(2, 1, 1);
    BlockPos minerPos = new BlockPos(3, 1, 1);

    // 木エンジンを設置
    helper.setBlock(enginePos, com.peco2282.bcreborn.core.CoreBlocks.WOODEN_ENGINE.get().defaultBlockState()
            .setValue(com.peco2282.bcreborn.common.block.EngineBlock.FACING, Direction.EAST));

    // パイプを設置
    helper.setBlock(woodPipePos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.WOOD).get().defaultBlockState());
    helper.setBlock(goldPipePos, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.GOLD).get().defaultBlockState());

    // Minerを設置
    helper.setBlock(minerPos, com.peco2282.bcreborn.factory.FactoryBlocks.MINING_WELL.get().defaultBlockState());

    // エンジンにレッドストーン信号を与える
    helper.setBlock(enginePos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());

    helper.runAtTickTime(100, () -> {
        // 木エンジンの熱が上がってエネルギーが生成されるのを待つ
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
        if (!(be instanceof com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity)) helper.fail("Miner not found");
        com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity minerBE = (com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity) be;
        int stored = minerBE.getBattery().getEnergyStored();
        // 100tick後にはエネルギーが蓄積されているはず
        if (stored <= 0) helper.fail("Energy not reaching miner. Current stored: " + stored);
    });
  }
}
