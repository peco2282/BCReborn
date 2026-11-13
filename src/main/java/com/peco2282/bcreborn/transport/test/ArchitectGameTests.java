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
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersItems;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.builders.item.ConstructionMarkerBlockItem;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.blueprint.Blueprint;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the Architect Table against the original 7.1.x {@code TileArchitect} / {@code RecursiveBlueprintReader}:
 * a blank blueprint in slot 0 is replaced by the recorded blueprint in slot 1, and a Builder can rebuild it.
 * <p>
 * Both machines face WEST (blueprint direction EAST), so nothing is rotated. The Architect sits at the same offset
 * from SOURCE as the Builder from TARGET.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class ArchitectGameTests {
  private static final String TEMPLATE = "quarry_13x8x13";
  private static final BlockPos BUILDER = new BlockPos(1, 2, 1);
  private static final BlockPos TARGET = new BlockPos(3, 2, 3);
  private static final BlockPos ARCHITECT = new BlockPos(6, 2, 6);
  private static final BlockPos SOURCE = new BlockPos(8, 2, 8);
  private static final BlockPos SUB_ARCHITECT = new BlockPos(10, 2, 4);
  private static final BlockPos SUB_SOURCE = new BlockPos(11, 2, 4);

  /** Places an Architect Table whose box covers {@code min..max} (as if taken from landmarks). */
  private static ArchitectBlockEntity placeArchitect(GameTestHelper helper, BlockPos pos, BlockPos min, BlockPos max) {
    helper.setBlock(pos, BuildersBlock.ARCHITECT.get().defaultBlockState()
      .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.WEST));
    ArchitectBlockEntity architect = (ArchitectBlockEntity) helper.getBlockEntity(pos);
    if (min != null) {
      BlockPos a = helper.absolutePos(min);
      BlockPos b = helper.absolutePos(max);
      architect.box.initialize(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
    }
    return architect;
  }

  private static void fillSource(GameTestHelper helper) {
    helper.setBlock(SOURCE, Blocks.STONE_BRICKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(1, 0, 0), Blocks.OAK_PLANKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(0, 0, 1), Blocks.OAK_PLANKS.defaultBlockState());
    helper.setBlock(SOURCE.offset(1, 0, 1), Blocks.AIR.defaultBlockState());
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 800)
  public void architectRecordsBlueprintForBuilder(GameTestHelper helper) {
    fillSource(helper);
    for (int x = 0; x < 2; x++) {
      for (int z = 0; z < 2; z++) {
        helper.setBlock(TARGET.offset(x, 0, z), Blocks.AIR.defaultBlockState());
      }
    }
    ArchitectBlockEntity architect = placeArchitect(helper, ARCHITECT, SOURCE, SOURCE.offset(1, 0, 1));
    architect.name = "gametest_architect";
    architect.currentAuthorName = "GameTest";

    // The table enters COPY mode on its first tick; the blank blueprint is inserted after that.
    helper.runAfterDelay(3, () -> {
      if (architect.mode != ArchitectBlockEntity.Mode.COPY) {
        helper.fail("An Architect Table with a box must be in COPY mode, got " + architect.mode);
      }
      architect.setItem(0, new ItemStack(BuildersItems.BLUEPRINT_STANDARD.get()));
    });

    boolean[] builderPlaced = {false};
    helper.succeedWhen(() -> {
      ItemStack result = architect.getItem(1);
      helper.assertTrue(!result.isEmpty(), "The recorded blueprint must appear in the output slot");
      helper.assertTrue(architect.getItem(0).isEmpty(), "The blank blueprint must be consumed");

      if (!builderPlaced[0]) {
        BlueprintBase bpt = BlueprintItem.loadBlueprint(result);
        if (!(bpt instanceof Blueprint)) {
          helper.fail("The output must load as a blueprint from the database, got " + bpt);
        }
        if (bpt.sizeX != 2 || bpt.sizeY != 1 || bpt.sizeZ != 2) {
          helper.fail("Wrong blueprint size " + bpt.sizeX + "x" + bpt.sizeY + "x" + bpt.sizeZ);
        }
        CompoundTag tag = result.getOrCreateTag();
        if (!"gametest_architect".equals(tag.getString("name")) || !"GameTest".equals(tag.getString("author"))) {
          helper.fail("The blueprint item must carry the name and author, got " + tag);
        }

        helper.setBlock(BUILDER, BuildersBlock.BUILDER.get().defaultBlockState()
          .setValue(BuildCraftBlock.HORIZONTAL_FACING, Direction.WEST));
        BuilderBlockEntity builder = (BuilderBlockEntity) helper.getBlockEntity(BUILDER);
        builder.getBattery().setEnergy(builder.getBattery().getMaxEnergyStored());
        builder.setItem(1, new ItemStack(Items.STONE_BRICKS, 4));
        builder.setItem(2, new ItemStack(Items.OAK_PLANKS, 4));
        builder.setItem(0, result.copy());
        builderPlaced[0] = true;
      }

      helper.assertBlockPresent(Blocks.STONE_BRICKS, TARGET);
      helper.assertBlockPresent(Blocks.OAK_PLANKS, TARGET.offset(1, 0, 0));
      helper.assertBlockPresent(Blocks.OAK_PLANKS, TARGET.offset(0, 0, 1));
      helper.assertBlockPresent(Blocks.AIR, TARGET.offset(1, 0, 1));
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = TEMPLATE, templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void architectRecordsTemplateWithSubBlueprint(GameTestHelper helper) {
    fillSource(helper);
    helper.setBlock(SUB_SOURCE, Blocks.COBBLESTONE.defaultBlockState());
    ArchitectBlockEntity architect = placeArchitect(helper, ARCHITECT, SOURCE, SOURCE.offset(1, 0, 1));
    placeArchitect(helper, SUB_ARCHITECT, SUB_SOURCE, SUB_SOURCE);

    helper.runAfterDelay(3, () -> {
      // Original ItemConstructionMarker.link(): first click on the parent table, second on the sub table.
      ItemStack marker = new ItemStack(Items.STICK);
      ConstructionMarkerBlockItem.link(marker, helper.getLevel(), helper.absolutePos(ARCHITECT));
      ConstructionMarkerBlockItem.link(marker, helper.getLevel(), helper.absolutePos(SUB_ARCHITECT));
      if (architect.getSubBlueprints().size() != 1) {
        helper.fail("Linking must register the sub table, got " + architect.getSubBlueprints());
      }
      if (ConstructionMarkerBlockItem.linkStarted(marker)) {
        helper.fail("A finished link must clear the marker");
      }
      architect.setItem(0, new ItemStack(BuildersItems.BLUEPRINT_TEMPLATE.get()));
    });

    helper.succeedWhen(() -> {
      ItemStack result = architect.getItem(1);
      helper.assertTrue(!result.isEmpty(), "The recorded template must appear in the output slot");
      BlueprintBase bpt = BlueprintItem.loadBlueprint(result);
      helper.assertTrue(bpt instanceof Template, "The output must load as a template");
      helper.assertTrue(bpt.get(0, 0, 0) != null && bpt.get(1, 0, 1) == null,
        "The template mask must follow the solid blocks");
      helper.assertTrue(bpt.subBlueprintsNBT.size() == 1, "The template must contain the sub table's template");
      CompoundTag sub = bpt.subBlueprintsNBT.get(0);
      CompoundTag subBpt = sub.getCompound("bpt");
      helper.assertTrue(subBpt.getInt("sizeX") == 1 && subBpt.getInt("sizeZ") == 1,
        "The sub entry must hold the sub template, not the parent");
      helper.assertTrue(sub.getInt("x") == SUB_ARCHITECT.getX() - SOURCE.getX()
          && sub.getInt("z") == SUB_ARCHITECT.getZ() - SOURCE.getZ(),
        "The sub template is positioned relative to the parent box, got " + sub.getInt("x") + "," + sub.getInt("z"));
    });
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void architectWithoutBoxDoesNothing(GameTestHelper helper) {
    ArchitectBlockEntity architect = placeArchitect(helper, new BlockPos(1, 2, 1), null, null);
    helper.runAfterDelay(3, () -> architect.setItem(0, new ItemStack(BuildersItems.BLUEPRINT_STANDARD.get())));
    helper.runAfterDelay(30, () -> {
      if (architect.mode == ArchitectBlockEntity.Mode.COPY) {
        helper.fail("Without landmarks the table must not be in COPY mode");
      }
      if (architect.getItem(0).isEmpty() || !architect.getItem(1).isEmpty()) {
        helper.fail("Without a box the blank blueprint must stay in the input slot");
      }
      if (architect.canPlaceItem(0, new ItemStack(BuildersItems.BLUEPRINT_STANDARD.get()))) {
        helper.fail("Automation must not insert into the Architect Table");
      }
      helper.succeed();
    });
  }
}
