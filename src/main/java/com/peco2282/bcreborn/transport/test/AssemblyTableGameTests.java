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
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconBlocks;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.silicon.block.entity.AssemblyTableBlockEntity;
import com.peco2282.bcreborn.transport.TransportItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies recipe planning ported from the original 7.1.x {@code TileAssemblyTable}.
 */
@GameTestHolder(BCRebornTransport.MODID)
public class AssemblyTableGameTests {
  private static final BlockPos TABLE = new BlockPos(1, 1, 1);
  private static final ResourceLocation RED_WIRE = ResourceLocation.parse("bcrebornsilicon:red_pipe_wire");
  private static final ResourceLocation IRON_CHIPSET = ResourceLocation.parse("bcrebornsilicon:iron_chipset");

  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static AssemblyTableBlockEntity placeTable(GameTestHelper helper) {
    helper.setBlock(TABLE, SiliconBlocks.ASSEMBLY_TABLE.get().defaultBlockState());
    return (AssemblyTableBlockEntity) helper.getBlockEntity(TABLE);
  }

  private static void tick(GameTestHelper helper, AssemblyTableBlockEntity table) {
    BuildCraftBlockEntity.<AssemblyTableBlockEntity>ticker().tick(helper.getLevel(), table.getBlockPos(), table.getBlockState(), table);
  }

  private static int dropped(GameTestHelper helper, Item item) {
    return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(TABLE)).inflate(2)).stream()
      .filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void onlyPlannedRecipesAreCrafted(GameTestHelper helper) {
    check(helper, BuildcraftRecipeRegistry.assembly().getRecipe(RED_WIRE) != null, "Wire recipe must be generated");
    AssemblyTableBlockEntity table = placeTable(helper);
    table.setItem(0, new ItemStack(Items.RED_DYE));
    table.setItem(1, new ItemStack(Items.REDSTONE));
    table.setItem(2, new ItemStack(Items.IRON_INGOT));

    // Both the iron chipset (redstone + iron) and the red wire are possible.
    var potential = table.getPotentialOutputs().stream().map(r -> r.id()).toList();
    check(helper, potential.contains(RED_WIRE) && potential.contains(IRON_CHIPSET),
      "Both recipes must be listed as potential outputs, got " + potential);

    table.receiveLaserEnergy(1_000_000);
    tick(helper, table);
    check(helper, table.getItem(1).getCount() == 1 && !table.canCraft(),
      "Without a plan nothing must be crafted (the old port crafted the alphabetically first recipe)");

    table.planOutput(RED_WIRE);
    check(helper, table.isAssembling(RED_WIRE) && table.requiresLaserEnergy() == false,
      "Planning must make the wire the current recipe (energy is already stored)");
    tick(helper, table);
    check(helper, dropped(helper, TransportItems.PIPE_WIRE_RED.get()) == 8, "The planned wire must be crafted (8 wires)");
    check(helper, dropped(helper, SiliconItems.IRON_CHIPSET.get()) == 0, "The unplanned iron chipset must not be crafted");
    check(helper, table.getItem(0).isEmpty() && table.getItem(1).isEmpty() && table.getItem(2).isEmpty(),
      "The wire recipe must consume dye, redstone and iron");
    check(helper, !table.canCraft(), "With nothing left the table must have no current recipe");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void plannedRecipeStartsWhenIngredientsArriveAndPlanSurvivesReload(GameTestHelper helper) {
    AssemblyTableBlockEntity table = placeTable(helper);
    table.planOutput(IRON_CHIPSET);
    check(helper, table.isPlanned(IRON_CHIPSET), "The recipe must be planned");

    var display = table.getDisplayedRecipes();
    check(helper, display.size() == 1 && display.get(0).id().equals(IRON_CHIPSET) && !display.get(0).craftable(),
      "A planned recipe without ingredients must be shown as not craftable");

    table.setItem(0, new ItemStack(Items.REDSTONE));
    table.setItem(1, new ItemStack(Items.IRON_INGOT));
    tick(helper, table);
    check(helper, table.isAssembling(IRON_CHIPSET) && table.requiresLaserEnergy(),
      "Inserting the ingredients must start the planned recipe and request laser power");

    CompoundTag saved = table.saveWithoutMetadata();
    AssemblyTableBlockEntity restored = new AssemblyTableBlockEntity(table.getBlockPos(), table.getBlockState());
    restored.load(saved);
    check(helper, restored.isPlanned(IRON_CHIPSET) && restored.isAssembling(IRON_CHIPSET),
      "The plan and the current recipe must survive save/load");

    table.cancelPlanOutput(IRON_CHIPSET);
    check(helper, !table.isPlanned(IRON_CHIPSET) && !table.canCraft(), "Cancelling must clear the plan");
    helper.succeed();
  }
}
