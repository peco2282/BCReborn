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
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.builder.patterns.PatternParameterYDir;
import com.peco2282.bcreborn.common.registry.BCFillerPatterns;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies filler behaviour ported from the original 7.1.x {@code TileFiller}.
 * The box is set directly (landmark pickup is covered by {@link LandmarkGameTests}).
 */
@GameTestHolder(BCRebornTransport.MODID)
public class FillerGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos FILLER = new BlockPos(0, 2, 0);
  private static final BlockPos BOX_MIN = new BlockPos(2, 2, 2);
  private static final BlockPos BOX_MAX = new BlockPos(3, 2, 3);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static int indexOf(net.minecraft.resources.ResourceLocation id) {
    var patterns = BCFillerPatterns.collection();
    for (int i = 0; i < patterns.size(); i++) {
      if (patterns.get(i).getKey().location().equals(id)) return i;
    }
    throw new IllegalStateException("Pattern not registered: " + id);
  }

  private static FillerBlockEntity placeFiller(GameTestHelper helper) {
    helper.setBlock(FILLER, BuildersBlock.FILLER.get().defaultBlockState());
    FillerBlockEntity filler = (FillerBlockEntity) helper.getBlockEntity(FILLER);
    BlockPos min = helper.absolutePos(BOX_MIN);
    BlockPos max = helper.absolutePos(BOX_MAX);
    filler.box.initialize(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    filler.getBattery().addEnergy(0, filler.getBattery().getMaxEnergyStored(), false);
    return filler;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void everyPatternBuildsWithItsParameters(GameTestHelper helper) {
    FillerBlockEntity filler = placeFiller(helper);
    var ticker = BuildCraftBlockEntity.<FillerBlockEntity>ticker();
    var patterns = BCFillerPatterns.collection();
    check(helper, filler.getPattern() == BCFillerPatterns.FILL.get(), "Default pattern must be Fill like the original");
    for (int i = 0; i < patterns.size(); i++) {
      filler.setPattern(i);
      var pattern = patterns.get(i).getValue();
      IStatementParameter[] parameters = filler.getPatternParameters();
      check(helper, parameters.length == pattern.maxParameters(), "Parameter array must match maxParameters for " + pattern);
      for (int p = 0; p < pattern.minParameters(); p++) {
        check(helper, parameters[p] != null, "Required parameter " + p + " must default for " + pattern);
      }
      // Previously Cylinder, Pyramid and Stairs threw a NullPointerException here (parameters were null).
      ticker.tick(helper.getLevel(), filler.getBlockPos(), filler.getBlockState(), filler);
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void fillPatternFillsBoxThenStaysDoneAcrossReload(GameTestHelper helper) {
    FillerBlockEntity filler = placeFiller(helper);
    filler.setItem(0, new ItemStack(Items.COBBLESTONE, 16));
    helper.succeedWhen(() -> {
      filler.getBattery().addEnergy(0, filler.getBattery().getMaxEnergyStored(), false);
      for (int x = BOX_MIN.getX(); x <= BOX_MAX.getX(); x++) {
        for (int z = BOX_MIN.getZ(); z <= BOX_MAX.getZ(); z++) {
          helper.assertBlockPresent(Blocks.COBBLESTONE, new BlockPos(x, BOX_MIN.getY(), z));
        }
      }
      helper.assertTrue(filler.isDone(), "Filler must report done once the box is filled");

      CompoundTag saved = filler.saveWithoutMetadata();
      FillerBlockEntity restored = new FillerBlockEntity(filler.getBlockPos(), filler.getBlockState());
      restored.load(saved);
      helper.assertTrue(restored.isDone(), "A finished filler must stay done after reload (it used to restart)");
      helper.assertTrue(restored.getPattern() == BCFillerPatterns.FILL.get(), "Pattern must be restored by id");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void patternParametersAndExcavateSurviveReload(GameTestHelper helper) {
    FillerBlockEntity filler = placeFiller(helper);
    filler.setPattern(indexOf(BCFillerPatterns.PYRAMID.getId()));
    filler.setPatternParameter(0, new PatternParameterYDir(false));
    filler.setExcavate(false);

    CompoundTag saved = filler.saveWithoutMetadata();
    check(helper, saved.contains("pattern") && saved.getString("pattern").equals(BCFillerPatterns.PYRAMID.getId().toString()),
      "Pattern must be saved by id instead of registry index");
    FillerBlockEntity restored = new FillerBlockEntity(filler.getBlockPos(), filler.getBlockState());
    restored.load(saved);
    check(helper, restored.getPattern() == BCFillerPatterns.PYRAMID.get(), "Pyramid must be restored");
    check(helper, restored.getPatternParameters()[0] instanceof PatternParameterYDir dir && !dir.up,
      "Changed parameters must be restored");
    check(helper, !restored.isExcavate(), "The excavate flag must be saved");

    CompoundTag legacy = new CompoundTag();
    legacy.putInt("delta", indexOf(BCFillerPatterns.STAIRS.getId()));
    FillerBlockEntity old = new FillerBlockEntity(filler.getBlockPos(), filler.getBlockState());
    old.load(legacy);
    check(helper, old.getPattern() == BCFillerPatterns.STAIRS.get() && old.getPatternParameters()[0] != null,
      "Saves with the old registry index must still load, with default parameters");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void fillerControlModes(GameTestHelper helper) {
    FillerBlockEntity filler = placeFiller(helper);
    filler.setItem(0, new ItemStack(Items.COBBLESTONE, 16));
    check(helper, filler.acceptsControlMode(IControllable.Mode.Loop) && !filler.acceptsControlMode(IControllable.Mode.Mode),
      "Filler must accept Off / On / Loop like the original");
    filler.setControlMode(IControllable.Mode.Off);
    check(helper, !filler.hasWork(), "A filler switched Off has no work");
    helper.runAfterDelay(20, () -> {
      check(helper, filler.getItem(0).getCount() == 16, "A filler switched Off must not build");
      helper.succeed();
    });
  }
}
