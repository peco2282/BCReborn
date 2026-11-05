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
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.StackAtPosition;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.s2c.LaunchItemPacket;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the building item flight sync ported from the original 7.1.x {@code TileAbstractBuilder}
 * ({@code launchItem} / {@code uploadBuildersInAction}) and {@code BuildingItem}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class BuildingItemSyncGameTests {
  private static final BlockPos MACHINE = new BlockPos(1, 1, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /** A building item without a slot to build, flying 3 blocks east. */
  private static BuildingItem flyingItem(BlockPos from) {
    BuildingItem item = new BuildingItem();
    item.origin = new Position(from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5);
    item.destination = new Position(from.getX() + 3.5, from.getY() + 0.5, from.getZ() + 0.5);
    item.setStacksToDisplay(List.of(new ItemStack(Items.COBBLESTONE, 2)));
    return item;
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void launchPacketCarriesTheFlight(GameTestHelper helper) {
    BlockPos abs = helper.absolutePos(MACHINE);
    BuildingItem item = flyingItem(abs);
    item.initialize();
    for (int i = 0; i < 3; i++) item.update();

    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new LaunchItemPacket(abs, item).encode(buf);
    LaunchItemPacket decoded = LaunchItemPacket.decode(buf);
    BuildingItem copy = decoded.item();
    check(helper, decoded.pos().equals(abs), "The packet must name the machine");
    check(helper, copy.origin.x == item.origin.x && copy.destination.x == item.destination.x,
      "Origin and destination must be sent");
    List<ItemStack> stacks = copy.stacksToDisplay.stream().map((StackAtPosition s) -> s.stack).toList();
    check(helper, stacks.size() == 2 && stacks.stream().allMatch(s -> s.is(Items.COBBLESTONE)),
      "Every displayed stack must be sent, got " + stacks);
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void clientCopyFliesAndLandsWithoutBuilding(GameTestHelper helper) {
    helper.setBlock(MACHINE, BuildersBlock.FILLER.get().defaultBlockState());
    FillerBlockEntity filler = (FillerBlockEntity) helper.getBlockEntity(MACHINE);
    BlockPos abs = helper.absolutePos(MACHINE);
    // What LaunchItemPacket.handle() does on the client.
    BuildingItem received = flyingItem(abs);
    received.initialize();
    filler.getBuilders().add(received);

    boolean displayed = false;
    for (int tick = 0; tick < 200 && !filler.getBuilders().isEmpty(); tick++) {
      filler.tickClientBuildingItems();
      displayed |= received.getStacks().stream().anyMatch(s -> s.display);
    }
    check(helper, displayed, "The item must be displayed along its flight");
    check(helper, filler.getBuilders().isEmpty(), "A landed item must be forgotten on the client");
    check(helper, helper.getBlockState(MACHINE.east().east()).isAir(), "The client copy must not build anything");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 200)
  public void builderForgetsLandedItems(GameTestHelper helper) {
    helper.setBlock(MACHINE, BuildersBlock.BUILDER.get().defaultBlockState());
    BuilderBlockEntity builder = (BuilderBlockEntity) helper.getBlockEntity(MACHINE);
    builder.addAndLaunchBuildingItem(flyingItem(helper.absolutePos(MACHINE)));
    check(helper, builder.getBuilders().size() == 1, "The launched item must be in flight");
    // Previously the Builder kept its own list and never removed finished items.
    helper.succeedWhen(() -> helper.assertTrue(builder.getBuilders().isEmpty(), "The landed item must be removed"));
  }
}
