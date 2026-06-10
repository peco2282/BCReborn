package com.peco2282.bcreborn.common.data;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public abstract class BCRecipeHelper extends RecipeProvider {
  public BCRecipeHelper(PackOutput p_248933_) {
    super(p_248933_);
  }

  protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> item) {
    return shaped(category, item, 1);
  }

  protected ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> item, int count) {
    return ShapedRecipeBuilder.shaped(category, item.get(), count);
  }

  protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> item) {
    return shapeless(category, item, 1);
  }

  protected ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> item, int count) {
    return ShapelessRecipeBuilder.shapeless(category, item.get(), count);
  }

  protected ItemPredicate get(ItemLike... items) {
    return ItemPredicate.Builder.item().of(items).build();
  }

  protected ItemPredicate get(TagKey<Item> item) {
    return ItemPredicate.Builder.item().of(item).build();
  }
}
