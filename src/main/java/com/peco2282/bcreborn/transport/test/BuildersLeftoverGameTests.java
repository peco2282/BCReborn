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
import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.builders.quarry.QuarryChunkLoader;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.registry.BCFillerPatterns;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the remaining Builders features: Quarry chunk loading (original {@code TileQuarry.forceChunkLoading()})
 * and the Filler GUI controls (original {@code GuiFiller}: parameter slots and the excavate button).
 */
@GameTestHolder(BCRebornTransport.MODID)
public class BuildersLeftoverGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos QUARRY = new BlockPos(6, 2, 0);
  private static final BlockPos FILLER = new BlockPos(0, 2, 0);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /**
   * Forge only reports whether a ticket was newly added, so an "add" that returns false means the quarry already
   * holds that chunk. The probe ticket is removed again when it was added.
   */
  private static boolean isForcedByQuarry(ServerLevel level, BlockPos quarry, int chunkX, int chunkZ) {
    boolean added = ForgeChunkManager.forceChunk(level, BCRebornBuilders.MODID, quarry, chunkX, chunkZ, true, true);
    if (added) {
      ForgeChunkManager.forceChunk(level, BCRebornBuilders.MODID, quarry, chunkX, chunkZ, false, true);
    }
    return !added;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void quarryForcesAndReleasesItsChunks(GameTestHelper helper) {
    helper.setBlock(QUARRY, BuildersBlock.QUARRY.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.NORTH));
    QuarryBlockEntity quarry = (QuarryBlockEntity) helper.getBlockEntity(QUARRY);
    BlockPos abs = helper.absolutePos(QUARRY);
    ServerLevel level = helper.getLevel();

    helper.runAfterDelay(3, () -> {
      check(helper, quarry.box.isInitialized(), "The quarry must have set its area");
      check(helper, isForcedByQuarry(level, abs, abs.getX() >> 4, abs.getZ() >> 4),
        "quarryDoChunkLoading must keep the quarry chunk loaded");
      check(helper, isForcedByQuarry(level, abs, quarry.box.xMax >> 4, quarry.box.zMax >> 4),
        "Every chunk of the quarry area must be kept loaded");

      helper.setBlock(QUARRY, Blocks.AIR.defaultBlockState());
      check(helper, !isForcedByQuarry(level, abs, abs.getX() >> 4, abs.getZ() >> 4),
        "Breaking the quarry must release its chunks (original releaseTicket)");
      check(helper, !isForcedByQuarry(level, abs, quarry.box.xMax >> 4, quarry.box.zMax >> 4),
        "Breaking the quarry must release the area chunks too");
      helper.succeed();
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void quarryAreaLimitMatchesOriginalTicketDepth(GameTestHelper helper) {
    // Original: (xSize * zSize) >> 8 >= maxChunkListDepth (25) falls back to the default area.
    check(helper, QuarryChunkLoader.isTooLarge(81, 81), "81 x 81 covers 25 chunk areas and must be refused");
    check(helper, !QuarryChunkLoader.isTooLarge(79, 79), "79 x 79 must still be allowed");
    check(helper, QuarryChunkLoader.chunksFor(BlockPos.ZERO, new com.peco2282.bcreborn.common.Box()).size() == 1,
      "Without an area only the quarry chunk is loaded");
    check(helper, QuarryChunkLoader.chunksFor(new BlockPos(0, 0, 0), new com.peco2282.bcreborn.common.Box(0, 0, 0, 20, 4, 20))
      .contains(ChunkPos.asLong(1, 1)), "The area chunks must be included");
    helper.succeed();
  }

  private static int indexOf(net.minecraft.resources.ResourceLocation id) {
    var patterns = BCFillerPatterns.collection();
    for (int i = 0; i < patterns.size(); i++) {
      if (patterns.get(i).getKey().location().equals(id)) return i;
    }
    throw new IllegalStateException("Pattern not registered: " + id);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID)
  public void fillerGuiChangesParametersAndExcavate(GameTestHelper helper) {
    helper.setBlock(FILLER, BuildersBlock.FILLER.get().defaultBlockState());
    FillerBlockEntity filler = (FillerBlockEntity) helper.getBlockEntity(FILLER);
    filler.setPattern(indexOf(BCFillerPatterns.PYRAMID.getId()));
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "FillerGuiTest"));
    FillerMenu menu = new FillerMenu(1, player.getInventory(), filler);

    IStatementParameter[] before = filler.getPatternParameters();
    check(helper, before.length == 2 && "direction.up".equals(before[0].getDescription()),
      "Pyramid starts with an upward Y direction parameter");

    check(helper, menu.clickMenuButton(player, FillerMenu.parameterButtonId(0, 0, false)), "Parameter click must be accepted");
    check(helper, "direction.down".equals(filler.getPatternParameters()[0].getDescription()),
      "Clicking the Y direction parameter must flip it (original onClick)");
    check(helper, !menu.clickMenuButton(player, FillerMenu.parameterButtonId(3, 0, false)),
      "Pyramid has no fourth parameter");

    boolean excavate = filler.isExcavate();
    check(helper, menu.clickMenuButton(player, FillerMenu.BUTTON_EXCAVATE), "Excavate click must be accepted");
    check(helper, filler.isExcavate() != excavate, "The excavate button must toggle excavation (original setFlags)");

    var saved = filler.saveWithoutMetadata();
    FillerBlockEntity restored = new FillerBlockEntity(filler.getBlockPos(), filler.getBlockState());
    restored.load(saved);
    check(helper, "direction.down".equals(restored.getPatternParameters()[0].getDescription())
        && restored.isExcavate() == filler.isExcavate(), "Parameters and excavate must be saved");
    helper.succeed();
  }
}
