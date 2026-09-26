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
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.block.entity.WoodEngineBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BCRebornTransport.MODID)
@PrefixGameTestTemplate(false)
public class TransportCompatibilityGameTests {
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void speedsChangeOnEntryOnly(GameTestHelper helper) {
    PipeMaterial[] materials = {PipeMaterial.GOLD, PipeMaterial.STONE, PipeMaterial.COBBLESTONE};
    float[] expected = {0.15f, 0.045f, 0.04f};
    for (int i = 0; i < materials.length; i++) {
      helper.setBlock(new BlockPos(i, 1, 1), TransportBlocks.get(PipeType.ITEM, materials[i]).get().defaultBlockState());
    }
    helper.runAtTickTime(5, () -> {
      for (int i = 0; i < materials.length; i++) {
        PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(new BlockPos(i, 1, 1));
        pipe.injectItemWithSpeed(new ItemStack(Items.DIAMOND), Direction.DOWN, 0.05f);
        assertNear(helper, expected[i], pipe.getTravelingItems().get(0).getSpeed());
      }
    });
    helper.runAtTickTime(7, () -> {
      for (int i = 0; i < materials.length; i++) {
        PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(new BlockPos(i, 1, 1));
        assertNear(helper, expected[i], pipe.getTravelingItems().get(0).getSpeed());
      }
      helper.succeed();
    });
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void powerSplitExcludesInputAndUsesRemainingDemand(GameTestHelper helper) {
    BlockPos center = new BlockPos(1, 1, 1);
    helper.setBlock(center, TransportBlocks.get(PipeType.ENERGY, PipeMaterial.DIAMOND).get().defaultBlockState());
    for (Direction dir : new Direction[]{Direction.WEST, Direction.NORTH, Direction.SOUTH}) {
      helper.setBlock(center.relative(dir), TransportBlocks.get(PipeType.ENERGY, PipeMaterial.DIAMOND).get().defaultBlockState());
    }
    helper.runAtTickTime(5, () -> {
      PipeBlockEntity pipe = (PipeBlockEntity) helper.getBlockEntity(center);
      EnergyTransportModule module = pipe.getEnergyTransportModule();
      CompoundTag data = new CompoundTag();
      CompoundTag energy = new CompoundTag();
      energy.putDouble("internalPower" + Direction.WEST.ordinal(), 0);
      energy.putDouble("internalNextPower" + Direction.WEST.ordinal(), 100);
      energy.putInt("nextPowerQuery" + Direction.WEST.ordinal(), 100);
      energy.putInt("nextPowerQuery" + Direction.NORTH.ordinal(), 50);
      energy.putInt("nextPowerQuery" + Direction.SOUTH.ordinal(), 50);
      data.put("EnergyTransport", energy);
      module.load(data);
      module.tick(helper.getLevel(), pipe.getBlockPos());
      for (Direction dir : new Direction[]{Direction.WEST, Direction.NORTH, Direction.SOUTH}) {
        PipeBlockEntity neighbor = (PipeBlockEntity) helper.getBlockEntity(center.relative(dir));
        CompoundTag saved = new CompoundTag();
        neighbor.getEnergyTransportModule().save(saved);
        double received = saved.getCompound("EnergyTransport").getDouble("internalNextPower" + dir.getOpposite().ordinal());
        assertNear(helper, dir == Direction.WEST ? 0 : 50, received);
      }
      assertNear(helper, 0, module.getInternalPower()[Direction.WEST.ordinal()]);
      helper.succeed();
    });
  }

  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void woodEngineCannotBeExtractedAsForgeEnergy(GameTestHelper helper) {
    BlockPos pos = new BlockPos(1, 1, 1);
    helper.setBlock(pos, CoreBlocks.WOODEN_ENGINE.get().defaultBlockState());
    helper.setBlock(pos.below(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    helper.runAtTickTime(5, () -> {
      WoodEngineBlockEntity engine = (WoodEngineBlockEntity) helper.getBlockEntity(pos);
      if (engine.getCapability(ForgeCapabilities.ENERGY).isPresent()) helper.fail("Wood engine exposes FE capability");
      for (Direction side : Direction.values()) {
        if (engine.getCapability(ForgeCapabilities.ENERGY, side).isPresent()) helper.fail("Wood engine exposes sided FE capability");
      }
      int initialEnergy = engine.getEnergyStored();
      helper.runAtTickTime(37, () -> {
        assertNear(helper, initialEnergy + 20, engine.getEnergyStored());
        assertNear(helper, 1000, engine.getMaxEnergyStored());
        helper.succeed();
      });
    });
  }

  private static void assertNear(GameTestHelper helper, double expected, double actual) {
    if (Math.abs(expected - actual) > 0.00001) {
      helper.fail("Expected " + expected + " but got " + actual);
    }
  }
}
