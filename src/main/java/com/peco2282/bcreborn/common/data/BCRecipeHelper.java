package com.peco2282.bcreborn.common.data;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public abstract class BCRecipeHelper {

  protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... predicate) {
    return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, predicate);
  }

  protected static String getHasName(ItemLike item) {
    return "has_" + getItemName(item);
  }

  protected static String getItemName(ItemLike item) {
    return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
  }

  protected static InventoryChangeTrigger.TriggerInstance has(ItemLike p_125978_) {
    return inventoryTrigger(ItemPredicate.Builder.item().of(p_125978_).build());
  }

  protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
    return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
  }

  protected static ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> item) {
    return shaped(category, item, 1);
  }

  protected static ShapedRecipeBuilder shaped(RecipeCategory category, Supplier<? extends ItemLike> item, int count) {
    return ShapedRecipeBuilder.shaped(category, item.get(), count);
  }

  protected static ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> item) {
    return shapeless(category, item, 1);
  }

  protected static ShapelessRecipeBuilder shapeless(RecipeCategory category, Supplier<? extends ItemLike> item, int count) {
    return ShapelessRecipeBuilder.shapeless(category, item.get(), count);
  }

  protected static ItemPredicate get(ItemLike... items) {
    return ItemPredicate.Builder.item().of(items).build();
  }

  protected static ItemPredicate get(TagKey<Item> item) {
    return ItemPredicate.Builder.item().of(item).build();
  }
}
