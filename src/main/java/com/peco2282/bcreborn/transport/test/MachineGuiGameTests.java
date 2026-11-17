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
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.library.LibraryAPI;
import com.peco2282.bcreborn.api.library.LibraryTypeHandler;
import com.peco2282.bcreborn.api.library.LibraryTypeHandlerNBT;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.BptContext;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.blueprint.Template;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.item.ListItem;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.map.MapManager;
import com.peco2282.bcreborn.robotics.map.MapUtils;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import com.peco2282.bcreborn.robotics.zone.ZoneChunk;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import com.peco2282.bcreborn.common.packet.c2s.RequestZonePlanComputeMapPacket;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * The server side of the machines whose GUI used to be unconnected: the Requester (original {@code GuiRequester}),
 * the Zone Planner ({@code TileZonePlan}, {@code ContainerZonePlan}), the Electronic Library
 * ({@code TileBlueprintLibrary}) and the List.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class MachineGuiGameTests {
  private static final BlockPos POS = new BlockPos(1, 2, 1);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static FakePlayer player(GameTestHelper helper, String name) {
    FakePlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), name));
    BlockPos abs = helper.absolutePos(POS);
    player.setPos(abs.getX() + 0.5, abs.getY() + 1, abs.getZ() + 0.5);
    return player;
  }

  /** Original GuiRequester: clicking a request slot sets it to the cursor stack (with its count) or clears it. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void requesterRequestIsSetFromCursor(GameTestHelper helper) {
    helper.setBlock(POS, RoboticsBlocks.REQUESTER.get().defaultBlockState());
    RequesterBlockEntity requester = (RequesterBlockEntity) helper.getBlockEntity(POS);
    FakePlayer player = player(helper, "requester");
    RequesterMenu menu = (RequesterMenu) requester.createMenu(1, player.getInventory(), player);

    menu.setCarried(new ItemStack(Items.IRON_INGOT, 12));
    check(helper, menu.clickMenuButton(player, 7), "A request slot click must be accepted");
    check(helper, requester.getRequestTemplate(7).is(Items.IRON_INGOT) && requester.getRequestTemplate(7).getCount() == 12,
      "The request must be the cursor stack with its count, got " + requester.getRequestTemplate(7));
    check(helper, menu.getCarried().getCount() == 12, "Setting a request must not take the cursor stack");
    check(helper, requester.getRequest(7).getCount() == 12, "The request must be open for robots");

    menu.setCarried(ItemStack.EMPTY);
    menu.clickMenuButton(player, 7);
    check(helper, requester.getRequestTemplate(7).isEmpty(), "An empty cursor must clear the request");
    check(helper, !menu.clickMenuButton(player, RequesterBlockEntity.NB_ITEMS), "Only the request slots are buttons");
    helper.succeed();
  }

  /** Original TileZonePlan: a map in slot 0 is written with the selected zone and the planner's name. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 300)
  public void zonePlannerWritesSelectedZoneOntoMap(GameTestHelper helper) {
    helper.setBlock(POS, RoboticsBlocks.ZONE_PLAN.get().defaultBlockState());
    ZonePlanBlockEntity planner = (ZonePlanBlockEntity) helper.getBlockEntity(POS);
    FakePlayer player = player(helper, "planner");
    check(helper, planner.createMenu(1, player.getInventory(), player) instanceof ZonePlanMenu, "The planner must open its menu (it returned null)");

    ZonePlan zone = new ZonePlan();
    for (int x = 100; x < 104; x++) zone.set(x, -50, true);
    planner.selectArea(3);
    planner.setArea(3, zone);
    planner.doSetName("Farm");
    planner.setItem(ZonePlanBlockEntity.SLOT_INPUT, new ItemStack(CoreItems.MAP_LOCATION.get()));

    helper.succeedWhen(() -> {
      ItemStack map = planner.getItem(ZonePlanBlockEntity.SLOT_OUTPUT);
      helper.assertTrue(!map.isEmpty(), "The map is not written yet, progress " + planner.progress);
      IZone written = ((MapLocationItem) map.getItem()).getZone(map);
      helper.assertTrue(written instanceof ZonePlan, "The map must hold a zone, got " + written);
      helper.assertTrue(written.contains(101.5, 64, -49.5) && !written.contains(99.5, 64, -49.5),
        "The written zone must be the painted one");
      helper.assertTrue(map.getHoverName().getString().equals("Farm"), "The map must take the planner's name, got " + map.getHoverName().getString());
      helper.assertTrue(planner.getItem(ZonePlanBlockEntity.SLOT_INPUT).isEmpty(), "The blank map must be used up");
    });
  }

  /** Original importMap(): a map with a zone put into slot 2 replaces the selected zone. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void zonePlannerImportsZoneFromMap(GameTestHelper helper) {
    helper.setBlock(POS, RoboticsBlocks.ZONE_PLAN.get().defaultBlockState());
    ZonePlanBlockEntity planner = (ZonePlanBlockEntity) helper.getBlockEntity(POS);
    ZonePlan zone = new ZonePlan();
    zone.set(-20, 30, true);
    ItemStack map = new ItemStack(CoreItems.MAP_LOCATION.get());
    MapLocationItem.setZone(map, zone);

    planner.selectArea(5);
    planner.setItem(ZonePlanBlockEntity.SLOT_IMPORT, map);
    ZonePlan imported = planner.getArea(5);
    check(helper, imported != null && imported.get(-20, 30) && !imported.get(-21, 30), "The map's zone must become the selected zone");
    check(helper, planner.getArea(4) == null, "Other zones stay untouched");
    check(helper, !planner.canPlaceItem(ZonePlanBlockEntity.SLOT_OUTPUT, map) && !planner.canPlaceItem(ZonePlanBlockEntity.SLOT_INPUT, new ItemStack(Items.PAPER)),
      "Only maps go into the planner, and nothing into its output");

    CompoundTag saved = planner.saveWithFullMetadata();
    planner.load(saved);
    check(helper, planner.getCurrentSelectedArea() == 5 && planner.getArea(5).get(-20, 30), "The zones and the selection must be saved");
    helper.succeed();
  }

  /** ContainerZonePlan.computeMap(): the map colours of the block columns around the planner. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void zonePlannerComputesMapImage(GameTestHelper helper) {
    check(helper, MapManager.get() != null, "The map manager must run while the server runs");
    helper.setBlock(POS, RoboticsBlocks.ZONE_PLAN.get().defaultBlockState());
    ZonePlanBlockEntity planner = (ZonePlanBlockEntity) helper.getBlockEntity(POS);
    BlockPos abs = helper.absolutePos(POS);
    byte[] image = RequestZonePlanComputeMapPacket.computeMap(planner, abs.getX(), abs.getZ(), 16, 16, 1.0f);
    int mapped = 0;
    for (byte b : image) if (b != MapColor.NONE.id) mapped++;
    check(helper, mapped > 0, "The loaded chunks around the planner must be mapped");

    BlockPos far = abs.offset(ZonePlanBlockEntity.RESOLUTION * 2, 0, 0);
    byte[] outside = RequestZonePlanComputeMapPacket.computeMap(planner, far.getX(), far.getZ(), 4, 4, 1.0f);
    for (byte b : outside) check(helper, b == 0, "Outside the planner's area the map is empty");
    helper.succeed();
  }

  /** Small fixes found on the way: region ids with negative coordinates, clearing a full zone chunk. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void mapAndZoneHelpersRoundTrip(GameTestHelper helper) {
    for (int[] c : new int[][]{{-3, 7}, {5, -9}, {-1, -1}, {0, 0}}) {
      long id = MapUtils.getIDFromCoords(c[0], c[1]);
      check(helper, MapUtils.getXFromID(id) == c[0] && MapUtils.getZFromID(id) == c[1],
        "Region id must decode back to " + c[0] + "," + c[1] + ", got " + MapUtils.getXFromID(id) + "," + MapUtils.getZFromID(id));
    }
    ZoneChunk chunk = new ZoneChunk();
    for (int x = 0; x < 16; x++) for (int z = 0; z < 16; z++) chunk.set(x, z, true);
    chunk.set(0, 0, false);
    check(helper, chunk.get(15, 15) && !chunk.get(0, 0), "Clearing one block of a full chunk must keep the last block");
    ZonePlan zone = new ZonePlan();
    zone.set(40, 40, true);
    check(helper, zone.distanceToSquared(new BlockPos(40, 0, 40)) < 100, "The distance to a zone is to its chunk centre, got " + zone.distanceToSquared(new BlockPos(40, 0, 40)));
    helper.succeed();
  }

  /**
   * Original TileBlueprintLibrary: an item put into slot 0 is stored to the player after the progress time and
   * moves on to slot 1; an upload is written onto the item in slot 2, which moves to slot 3.
   */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 300)
  public void libraryStoresAndLoadsBlueprints(GameTestHelper helper) {
    helper.setBlock(POS, BuildersBlock.BLUEPRINT_LIBRARY.get().defaultBlockState());
    BlueprintLibraryBlockEntity library = (BlueprintLibraryBlockEntity) helper.getBlockEntity(POS);

    Template template = new Template(1, 1, 1);
    BlockPos abs = helper.absolutePos(POS.above());
    BptContext context = template.getContext(helper.getLevel(), new Box(abs, abs));
    template.readFromWorld(context, library, abs.getX(), abs.getY(), abs.getZ());
    template.id.name = "gametest_library";
    template.author = "GameTest";
    BCRebornBuilders.getServerDB().add(template.id, template.getNBT());
    ItemStack recorded = template.getStack();
    library.setItem(0, recorded);
    check(helper, library.progressIn == 1, "A readable blueprint must start the store progress");

    // The upload half: what the client would send for the same template.
    LibraryTypeHandler handler = LibraryAPI.getHandlerFor("tpl");
    CompoundTag nbt = new CompoundTag();
    check(helper, handler instanceof LibraryTypeHandlerNBT nbtHandler && nbtHandler.store(recorded, nbt), "The template must be storable");
    byte[] data = BlueprintLibraryBlockEntity.compress(nbt);
    library.setItem(2, new ItemStack(recorded.getItem()));
    LibraryId id = new LibraryId();
    id.name = "gametest_library";
    id.extension = "tpl";
    library.beginUpload(id, data.length);
    library.setBlueprintDownload(0, data);
    library.finishUpload();
    check(helper, library.getItem(2).isEmpty() && !library.getItem(3).isEmpty(), "The upload must be written onto the item and move it to slot 3");
    check(helper, library.getBlueprintDownload() == null, "The upload buffer must be released");

    helper.succeedWhen(() -> {
      helper.assertTrue(library.getItem(0).isEmpty() && !library.getItem(1).isEmpty(),
        "The stored item must move on to slot 1, progress " + library.progressIn);
      helper.assertTrue(library.progressIn == 0, "The store progress must reset");
    });
  }

  /** LibraryBookTypeHandler: a written book keeps its title and pages through the library. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void libraryBookHandlerRoundTrip(GameTestHelper helper) {
    ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
    book.getOrCreateTag().putString("title", "Notes");
    book.getOrCreateTag().putString("author", "GameTest");
    ListTag pages = new ListTag();
    pages.add(StringTag.valueOf("{\"text\":\"page one\"}"));
    book.getOrCreateTag().put("pages", pages);

    LibraryTypeHandlerNBT handler = (LibraryTypeHandlerNBT) LibraryAPI.getHandlerFor("book");
    check(helper, handler.getName(book).equals("Notes"), "The book's library name must be its title, got '" + handler.getName(book) + "'");
    CompoundTag nbt = new CompoundTag();
    handler.store(book, nbt);
    ItemStack loaded = handler.load(new ItemStack(Items.WRITABLE_BOOK), nbt);
    check(helper, loaded.is(Items.WRITTEN_BOOK) && loaded.getTag() != null && loaded.getTag().getString("title").equals("Notes")
      && loaded.getTag().getList("pages", 8).size() == 1, "Loading must give back the written book, got " + loaded.getTag());
    helper.succeed();
  }

  /** The List: one item in 1.20; only lists that hold data of the old format open the old GUI. */
  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void listOpensTheNewGuiUnlessOldData(GameTestHelper helper) {
    ItemStack list = new ItemStack(CoreItems.LIST.get());
    check(helper, !ListItem.isOldList(list), "A new list must open the new GUI (it always opened the old one)");
    check(helper, !list.getHoverName().getString().isEmpty(), "An unnamed list must still have a name");
    ItemStack old = new ItemStack(CoreItems.LIST.get());
    old.getOrCreateTag().put("line[0]", new CompoundTag());
    old.getOrCreateTag().putBoolean("written", true);
    check(helper, ListItem.isOldList(old), "A list with old data must open the old GUI");
    ListItem.saveLabel(list, Component.literal("Ores"));
    check(helper, list.getHoverName().getString().equals("Ores"), "A labelled list shows its label");
    helper.succeed();
  }
}
