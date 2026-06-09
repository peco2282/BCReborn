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

import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.CoreItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class CoreRecipeProvider extends RecipeProvider {
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
  }

  ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item) {
    return shaped(category, item, 1);
  }

  ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item, int count) {
    return ShapedRecipeBuilder.shaped(category, item, count);
  }

  ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item) {
    return shapeless(category, item, 1);
  }

  ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item, int count) {
    return ShapelessRecipeBuilder.shapeless(category, item, count);
  }

  ItemPredicate get(ItemLike... items) {
    return ItemPredicate.Builder.item().of(items).build();
  }

  ItemPredicate get(TagKey<Item> item) {
    return ItemPredicate.Builder.item().of(item).build();
  }
}
