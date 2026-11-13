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

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersItems;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.BptContext;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the Builder ported from the original 7.1.x {@code TileBuilder} together with the blueprint library
 * ({@code LibraryDatabase}) and blueprint items ({@code Blueprint / Template.getStack()}).
 * <p>
 * A 2 x 1 x 2 structure at SOURCE is recorded, stored in the server database, and built by a Builder at BUILDER
 * into TARGET. The Builder faces WEST, so it builds towards EAST and the blueprint is not rotated.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class BuilderGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos BUILDER = new BlockPos(1, 2, 1);
  private static final BlockPos TARGET = new BlockPos(3, 2, 3);
  private static final BlockPos SOURCE = new BlockPos(8, 2, 8);

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  /** Records the 2 x 1 x 2 area at SOURCE (as the Architect does) and stores it in the server database. */
  private static ItemStack record(GameTestHelper helper, BlueprintBase bpt, String name, BuilderBlockEntity anchor) {
    BlockPos min = helper.absolutePos(SOURCE);
    Box box = new Box(min, min.offset(1, 0, 1));
    BptContext context = bpt.getContext(helper.getLevel(), box);
    for (int x = 0; x < 2; x++) {
      for (int z = 0; z < 2; z++) {
        bpt.readFromWorld(context, anchor, min.getX() + x, min.getY(), min.getZ() + z);
      }
    }
    // The builder takes the anchor's place: anchor -> target min offset as the Architect would record it.
    BlockPos builder = helper.absolutePos(BUILDER);
    BlockPos target = helper.absolutePos(TARGET);
    bpt.anchorX = builder.getX() - target.getX();
    bpt.anchorY = builder.getY() - target.getY();
    bpt.anchorZ = builder.getZ() - target.getZ();
    bpt.id.name = name;
    bpt.author = "GameTest";
    BCRebornBuilders.getServerDB().add(bpt.id, bpt.getNBT());
    return bpt.getStack();
  }

  private static BuilderBlockEntity placeBuilder(GameTestHelper helper) {
    helper.setBlock(BUILDER, BuildersBlock.BUILDER.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.WEST));
    BuilderBlockEntity builder = (BuilderBlockEntity) helper.getBlockEntity(BUILDER);
    builder.getBattery().setEnergy(builder.getBattery().getMaxEnergyStored());
    return builder;
  }

  private static void clearTarget(GameTestHelper helper) {
    for (int x = 0; x < 2; x++) {
      for (int z = 0; z < 2; z++) {
        helper.setBlock(TARGET.offset(x, 0, z), Blocks.AIR.defaultBlockState());
      }
    }
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 600)
  public void builderBuildsTemplateWithAnyMaterial(GameTestHelper helper) {
    clearTarget(helper);
    for (int x = 0; x < 2; x++) {
      for (int z = 0; z < 2; z++) {
        helper.setBlock(SOURCE.offset(x, 0, z), Blocks.COBBLESTONE.defaultBlockState());
      }
    }
    BuilderBlockEntity builder = placeBuilder(helper);
    ItemStack stack = record(helper, new Template(2, 1, 2), "gametest_template", builder);
    check(helper, stack.is(BuildersItems.BLUEPRINT_TEMPLATE.get()), "A template must give a template item (getStack was a stub)");
    check(helper, BlueprintItem.loadBlueprint(stack) instanceof Template, "The stored template must load back from the database");

    builder.setItem(1, new ItemStack(Items.DIRT, 16));
    builder.setItem(0, stack);

    helper.succeedWhen(() -> {
      for (int x = 0; x < 2; x++) {
        for (int z = 0; z < 2; z++) {
          helper.assertBlockPresent(Blocks.DIRT, TARGET.offset(x, 0, z));
        }
      }
      helper.assertTrue(builder.getItem(0).isEmpty(), "The finished template must leave the blueprint slot");
      helper.assertTrue(builder.isDone(), "The builder must be done");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 600)
  public void builderBuildsBlueprintFromInventory(GameTestHelper helper) {
    clearTarget(helper);
    helper.setBlock(SOURCE, Blocks.STONE_BRICKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(1, 0, 0), Blocks.OAK_PLANKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(0, 0, 1), Blocks.OAK_PLANKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(1, 0, 1), Blocks.AIR.defaultBlockState());
    BuilderBlockEntity builder = placeBuilder(helper);
    ItemStack stack = record(helper, new Blueprint(2, 1, 2), "gametest_blueprint", builder);
    check(helper, stack.is(BuildersItems.BLUEPRINT_STANDARD.get()), "A blueprint must give a blueprint item");

    builder.setItem(1, new ItemStack(Items.STONE_BRICKS, 4));
    builder.setItem(2, new ItemStack(Items.OAK_PLANKS, 4));
    builder.setItem(0, stack);

    helper.succeedWhen(() -> {
      helper.assertBlockPresent(Blocks.STONE_BRICKS, TARGET);
      helper.assertBlockPresent(Blocks.OAK_PLANKS, TARGET.offset(1, 0, 0));
      helper.assertBlockPresent(Blocks.OAK_PLANKS, TARGET.offset(0, 0, 1));
      helper.assertTrue(builder.getItem(1).getCount() == 3 && builder.getItem(2).getCount() == 2,
        "The blueprint must use the exact materials, got " + builder.getItem(1) + " / " + builder.getItem(2));
      helper.assertTrue(builder.getItem(0).isEmpty(), "The finished blueprint must leave the blueprint slot");
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void blueprintLibrarySurvivesRefresh(GameTestHelper helper) {
    Template template = new Template(1, 1, 1);
    template.id.name = "gametest_refresh";
    template.author = "GameTest";
    BCRebornBuilders.getServerDB().add(template.id, template.getNBT());
    BCRebornBuilders.getServerDB().refresh();
    check(helper, BCRebornBuilders.getServerDB().exists(template.id),
      "A stored blueprint must be written to disk and found again after a refresh (save() never wrote the file)");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void builderWithoutBlueprintIsIdle(GameTestHelper helper) {
    helper.setBlock(new BlockPos(1, 1, 1), BuildersBlock.BUILDER.get().defaultBlockState());
    BuilderBlockEntity builder = (BuilderBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
    builder.setItem(0, new ItemStack(BuildersItems.BLUEPRINT_TEMPLATE.get()));
    helper.runAfterDelay(3, () -> {
      check(helper, builder.getBlueprint() == null && !builder.getBox().isInitialized(),
        "A blank blueprint item has nothing to build");
      check(helper, builder.getItem(0).isEmpty() && builder.getItem(1).is(BuildersItems.BLUEPRINT_TEMPLATE.get()),
        "Original iterateBpt(): with nothing to build the blueprint moves to the first free slot");
      helper.succeed();
    });
  }
}
