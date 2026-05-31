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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
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
    helper.setBlock(pos1, BlocksTransport.PIPES_BY_MAT.get(PipeMaterial.COBBLESTONE).get(PipeType.ITEM).get().defaultBlockState());
    helper.setBlock(pos2, BlocksTransport.PIPES_BY_MAT.get(PipeMaterial.COBBLESTONE).get(PipeType.ITEM).get().defaultBlockState());

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
      IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
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
}
