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
import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.LaserKind;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.core.CoreBlocks;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the landmark behaviour ported from the original 7.1.x {@code TileMarker} and
 * {@code Utils.getNearbyAreaProvider()}.
 * <p>
 * Registered under the transport namespace because it is the only GameTest namespace enabled in the run configs.
 * Each test runs in its own batch: landmarks search up to 64 blocks, so tests running side by side would link
 * to each other's landmarks.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class LandmarkGameTests {
  private static final BlockPos A = new BlockPos(0, 1, 0);
  private static final BlockPos B = new BlockPos(2, 1, 0);
  private static final BlockPos C = new BlockPos(0, 1, 2);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /**
   * Removes this test's landmarks before succeeding. Test structures of earlier batches stay in the world, so
   * leftover landmarks would be found by later tests' 64-block search.
   */
  private static void finish(GameTestHelper helper) {
    for (BlockPos pos : new BlockPos[]{A, B, C}) {
      helper.setBlock(pos, Blocks.AIR.defaultBlockState());
    }
    helper.succeed();
  }

  private static MarkerBlockEntity placeMarker(GameTestHelper helper, BlockPos pos) {
    helper.setBlock(pos.below(), Blocks.STONE.defaultBlockState());
    helper.setBlock(pos, CoreBlocks.BLUE_MARKER.get().defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    MarkerBlockEntity marker = (MarkerBlockEntity) helper.getBlockEntity(pos);
    marker.initialize();
    return marker;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, batch = "landmark_landmarkslinkintoredbox")
  public void landmarksLinkIntoRedBox(GameTestHelper helper) {
    MarkerBlockEntity a = placeMarker(helper, A);
    MarkerBlockEntity b = placeMarker(helper, B);
    MarkerBlockEntity c = placeMarker(helper, C);

    a.tryConnection();

    check(helper, a.origin.isSet() && a.origin == b.origin && a.origin == c.origin,
      "Three landmarks must share one origin after right-clicking the corner");
    BlockPos min = helper.absolutePos(A);
    check(helper, a.xMin() == min.getX() && a.xMax() == min.getX() + 2
        && a.zMin() == min.getZ() && a.zMax() == min.getZ() + 2 && a.yMin() == a.yMax(),
      "Box must span the three landmarks");
    check(helper, !a.lasers.isEmpty() && a.lasers.stream().allMatch(l -> l != null && l.kind == LaserKind.Red),
      "The origin landmark must hold non-null red box lasers");
    check(helper, b.lasers.isEmpty() && c.lasers.isEmpty(), "Only the origin landmark holds the box lasers");
    finish(helper);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, batch = "landmark_breakingpartiallylinkedlandmarkdissolvesgroup")
  public void breakingPartiallyLinkedLandmarkDissolvesGroup(GameTestHelper helper) {
    MarkerBlockEntity a = placeMarker(helper, A);
    placeMarker(helper, B);
    a.tryConnection();
    // The GameTest world is reused between runs, so a landmark left over from an earlier run may also be linked.
    // What matters is that X links to B and at least one axis stays unlinked (the case that used to crash).
    check(helper, a.origin.isSet() && helper.absolutePos(B).equals(a.origin.vect[0].pos)
        && (!a.origin.vect[1].isSet() || !a.origin.vect[2].isSet()),
      "X must link to B and at least one axis must stay unlinked");

    // Previously threw a NullPointerException for the unlinked axes.
    helper.setBlock(B, Blocks.AIR.defaultBlockState());

    check(helper, !a.origin.isSet(), "Breaking a linked landmark must dissolve the group");
    check(helper, a.lasers.isEmpty(), "Box lasers must be cleared when the group dissolves");
    finish(helper);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, batch = "landmark_redstoneshowssignalsonunlinkedaxesonly")
  public void redstoneShowsSignalsOnUnlinkedAxesOnly(GameTestHelper helper) {
    MarkerBlockEntity a = placeMarker(helper, A);
    placeMarker(helper, B);

    helper.setBlock(A.above(), Blocks.REDSTONE_BLOCK.defaultBlockState());
    check(helper, a.showSignals && a.signals.size() == 6, "A lone powered landmark must show beams on all 3 axes");

    a.tryConnection();
    check(helper, a.signals.size() == 4, "Linking the X axis must remove its signal beams, got " + a.signals.size());
    check(helper, !a.lasers.isEmpty(), "Box lasers must stay independent of the redstone signal");

    helper.setBlock(A.above(), Blocks.AIR.defaultBlockState());
    check(helper, !a.showSignals && a.signals.isEmpty(), "Removing power must hide the signal beams");
    check(helper, !a.lasers.isEmpty(), "Box lasers must not disappear when power is removed");
    finish(helper);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, batch = "landmark_machinesfindtouchinglandmarkbox")
  public void machinesFindTouchingLandmarkBox(GameTestHelper helper) {
    MarkerBlockEntity a = placeMarker(helper, A);
    placeMarker(helper, B);
    placeMarker(helper, C);
    a.tryConnection();

    BlockPos touching = helper.absolutePos(new BlockPos(1, 1, -1));
    IAreaProvider found = Utils.getNearbyAreaProvider(helper.getLevel(), touching);
    check(helper, found != null && found.xMin() == a.xMin() && found.zMax() == a.zMax(),
      "A machine touching the box edge must find the landmark area");
    check(helper, Utils.getNearbyAreaProvider(helper.getLevel(), helper.absolutePos(new BlockPos(1, 1, 1))) == null,
      "A position inside the box must not be valid");
    check(helper, Utils.getNearbyAreaProvider(helper.getLevel(), helper.absolutePos(new BlockPos(-1, 1, -1))) == null,
      "A diagonal corner position must not be valid");
    finish(helper);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, batch = "landmark_landmarksyncroundtripandworldoriginwrapper")
  public void landmarkSyncRoundTripAndWorldOriginWrapper(GameTestHelper helper) {
    MarkerBlockEntity a = placeMarker(helper, A);
    placeMarker(helper, B);
    a.tryConnection();

    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    a.writeData(buf);
    MarkerBlockEntity client = new MarkerBlockEntity(a.getType(), a.getBlockPos(), a.getBlockState());
    client.readData(buf);
    check(helper, client.lasers.size() == a.lasers.size() && client.origin.isSet()
        && client.origin.vect[0].pos.equals(a.origin.vect[0].pos) && !client.origin.vect[1].isSet(),
      "Client sync must carry the origin and lasers without resolving other landmarks");

    check(helper, new MarkerBlockEntity.TileWrapper(BlockPos.ZERO).isSet(),
      "A landmark at (0, 0, 0) must still count as set");
    LaserData any = a.lasers.get(0);
    check(helper, any != null, "Lasers must never contain null entries");
    finish(helper);
  }
}
