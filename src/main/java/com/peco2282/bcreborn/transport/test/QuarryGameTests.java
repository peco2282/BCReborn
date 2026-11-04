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
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.FrameBlock;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies quarry behaviour ported from the original 7.1.x {@code TileQuarry} / {@code BlockQuarry}.
 * <p>
 * Uses a 13x8x13 template (stone at y = 0..1) so the default 11x11 frame stays inside the test area.
 * The quarry sits at (6, 2, 0) facing NORTH, so its box is x 1..11, y 2..6, z 1..11 and the first frame
 * position is directly south of it.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class QuarryGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos QUARRY = new BlockPos(6, 2, 0);
  private static final BlockPos FIRST_FRAME = QUARRY.south();

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static QuarryBlockEntity placeQuarry(GameTestHelper helper) {
    helper.setBlock(QUARRY, BuildersBlock.QUARRY.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.NORTH));
    QuarryBlockEntity quarry = (QuarryBlockEntity) helper.getBlockEntity(QUARRY);
    quarry.getBattery().addEnergy(0, quarry.getBattery().getMaxEnergyStored(), false);
    return quarry;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void quarryClearsBlockInFramePathAndBuildsFrame(GameTestHelper helper) {
    helper.setBlock(FIRST_FRAME, Blocks.COBBLESTONE.defaultBlockState());
    placeQuarry(helper);
    // Previously the quarry waited forever on an obstructed frame position.
    helper.succeedWhen(() -> helper.assertBlockPresent(BuildersBlock.FRAME.get(), FIRST_FRAME));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void quarryOffModeHoldsUntilSwitchedOn(GameTestHelper helper) {
    QuarryBlockEntity quarry = placeQuarry(helper);
    check(helper, quarry.acceptsControlMode(IControllable.Mode.Loop) && !quarry.acceptsControlMode(IControllable.Mode.Mode),
      "Quarry must accept Off / On / Loop like the original");
    quarry.setControlMode(IControllable.Mode.Off);
    helper.runAfterDelay(20, () -> {
      check(helper, !(helper.getBlockState(FIRST_FRAME).getBlock() instanceof FrameBlock),
        "A quarry switched Off must not build");
      check(helper, quarry.getStage() == QuarryBlockEntity.Stage.BUILDING, "Off must hold the current stage");
      quarry.setControlMode(IControllable.Mode.On);
      helper.succeedWhen(() -> helper.assertBlockPresent(BuildersBlock.FRAME.get(), FIRST_FRAME));
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 100)
  public void quarryLoopModeRestartsWhenDone(GameTestHelper helper) {
    QuarryBlockEntity quarry = placeQuarry(helper);
    helper.runAfterDelay(2, () -> {
      CompoundTag tag = quarry.saveWithoutMetadata();
      tag.putInt("stage", QuarryBlockEntity.Stage.DONE.ordinal());
      quarry.load(tag);
      quarry.setControlMode(IControllable.Mode.On);
      helper.runAfterDelay(3, () -> {
        check(helper, quarry.getStage() == QuarryBlockEntity.Stage.DONE, "On mode must stay DONE");
        quarry.setControlMode(IControllable.Mode.Loop);
        helper.runAfterDelay(3, () -> {
          check(helper, quarry.getStage() != QuarryBlockEntity.Stage.DONE,
            "Loop mode must restart a finished quarry while there is still stone to dig");
          helper.succeed();
        });
      });
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void quarryBatteryMatchesOriginal(GameTestHelper helper) {
    QuarryBlockEntity quarry = placeQuarry(helper);
    int stored = quarry.getBattery().getEnergyStored();
    check(helper, quarry.getBattery().extractEnergy(100, false) == 0,
      "Other blocks must not be able to pull energy out of a quarry (original maxExtract = 0)");
    check(helper, quarry.getBattery().getEnergyStored() == stored, "Extraction must not change the stored energy");
    helper.succeed();
  }
}
