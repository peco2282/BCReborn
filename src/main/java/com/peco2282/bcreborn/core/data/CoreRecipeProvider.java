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
package com.peco2282.bcreborn.core.data;

import com.peco2282.bcreborn.common.data.BCRecipeHelper;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Locale;
import java.util.function.Consumer;

public class CoreRecipeProvider extends BCRecipeHelper {
  public CoreRecipeProvider(PackOutput p_248933_) {
    super(p_248933_);
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> p_251297_) {

    shaped(RecipeCategory.MISC, CoreItems.WOODEN_GEAR.get())
      .pattern(" S ")
      .pattern("S S")
      .pattern(" S ")
      .define('S', Items.STICK)
      .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.STONE_GEAR.get())
      .pattern(" C ")
      .pattern("CGC")
      .pattern(" C ")
      .define('C', Items.COBBLESTONE)
      .define('G', CoreItems.WOODEN_GEAR.get())
      .unlockedBy(getHasName(CoreItems.WOODEN_GEAR.get()), inventoryTrigger(get(Items.COBBLESTONE, CoreItems.WOODEN_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.IRON_GEAR.get())
      .pattern(" I ")
      .pattern("IGI")
      .pattern(" I ")
      .define('I', Items.IRON_INGOT)
      .define('G', CoreItems.STONE_GEAR.get())
      .unlockedBy(getHasName(CoreItems.STONE_GEAR.get()), inventoryTrigger(get(Items.IRON_INGOT, CoreItems.STONE_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.GOLD_GEAR.get())
      .pattern(" G ")
      .pattern("GIG")
      .pattern(" G ")
      .define('G', Items.GOLD_INGOT)
      .define('I', CoreItems.IRON_GEAR.get())
      .unlockedBy(getHasName(CoreItems.IRON_GEAR.get()), inventoryTrigger(get(Items.GOLD_INGOT, CoreItems.IRON_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.DIAMOND_GEAR.get())
      .pattern(" D ")
      .pattern("DGD")
      .pattern(" D ")
      .define('D', Items.DIAMOND)
      .define('G', CoreItems.GOLD_GEAR.get())
      .unlockedBy(getHasName(CoreItems.GOLD_GEAR.get()), inventoryTrigger(get(Items.DIAMOND, CoreItems.GOLD_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.WRENCH.get())
      .pattern("I I")
      .pattern(" S ")
      .pattern(" I ")
      .define('I', Items.IRON_INGOT)
      .define('S', CoreItems.STONE_GEAR.get())
      .unlockedBy(getHasName(CoreItems.STONE_GEAR.get()), inventoryTrigger(get(Items.IRON_INGOT, CoreItems.STONE_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.MAP_LOCATION.get())
      .pattern("PPP")
      .pattern("PYP")
      .pattern("PPP")
      .define('P', Items.PAPER)
      .define('Y', Items.YELLOW_DYE)
      .unlockedBy("has_paper", inventoryTrigger(get(Items.PAPER)))
      .unlockedBy("has_yellow_dye", inventoryTrigger(get(Items.YELLOW_DYE)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreBlocks.WOODEN_ENGINE.get())
      .pattern("PPP")
      .pattern(" G ")
      .pattern("WPW")
      .define('P', ItemTags.PLANKS)
      .define('G', Items.GLASS)
      .define('W', CoreItems.WOODEN_GEAR.get())
      .unlockedBy("has_plank", inventoryTrigger(get(ItemTags.PLANKS)))
      .unlockedBy("has_glass", inventoryTrigger(get(Items.GLASS)))
      .unlockedBy("has_wooden_gear", inventoryTrigger(get(CoreItems.WOODEN_GEAR.get())))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreBlocks.BLUE_MARKER.get())
      .pattern("B")
      .pattern("R")
      .define('B', Items.BLUE_DYE)
      .define('R', Items.REDSTONE_TORCH)
      .unlockedBy("has_blue_dye", inventoryTrigger(get(Items.BLUE_DYE)))
      .unlockedBy("has_redstone_torch", inventoryTrigger(get(Items.REDSTONE_TORCH)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreBlocks.PATH_MARKER.get())
      .pattern("G")
      .pattern("R")
      .define('G', Items.GREEN_DYE)
      .define('R', Items.REDSTONE_TORCH)
      .unlockedBy("has_green_dye", inventoryTrigger(get(Items.GREEN_DYE)))
      .unlockedBy("has_redstone_torch", inventoryTrigger(get(Items.REDSTONE_TORCH)))
      .save(p_251297_);

    shaped(RecipeCategory.MISC, CoreItems.PAINTBRUSH.get())
      .pattern(" SW")
      .pattern(" GS")
      .pattern("T  ")
      .define('S', Items.STRING)
      .define('G', CoreItems.WOODEN_GEAR.get())
      .define('W', ItemTags.WOOL)
      .define('T', Items.STICK)
      .unlockedBy("has_string", inventoryTrigger(get(Items.STRING)))
      .unlockedBy("has_wooden_gear", inventoryTrigger(get(CoreItems.WOODEN_GEAR.get())))
      .unlockedBy("has_wool", inventoryTrigger(get(ItemTags.WOOL)))
      .save(p_251297_);

    CoreItems.COLORED_PAINTBRUSH.forEach((key, value) -> {
      //noinspection DataFlowIssue
      shapeless(RecipeCategory.MISC, value.get())
        .requires(value.get().getColoredItem())
        .requires(CoreItems.PAINTBRUSH.get())
        .unlockedBy("has_" + key.getSerializedName().toLowerCase(Locale.ROOT) + "_dye", inventoryTrigger(get(value.get().getColoredItem())))
        .unlockedBy("has_nocolor_brush", inventoryTrigger(get(CoreItems.PAINTBRUSH.get())))
        .save(p_251297_);
    });
  }


}
