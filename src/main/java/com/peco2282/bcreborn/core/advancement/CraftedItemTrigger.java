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
package com.peco2282.bcreborn.core.advancement;

import com.google.gson.JsonObject;
import com.peco2282.bcreborn.BCRebornCore;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Original {@code AchievementManager.onCrafting()}: an achievement is earned by crafting its item. The trigger fires
 * from {@code PlayerEvent.ItemCraftedEvent}, so picking the item up does not count.
 */
public class CraftedItemTrigger extends SimpleCriterionTrigger<CraftedItemTrigger.Instance> {
  public static final ResourceLocation ID = BCRebornCore.location("crafted_item");
  public static final CraftedItemTrigger INSTANCE = new CraftedItemTrigger();

  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  protected Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
    return new Instance(player, ItemPredicate.fromJson(json.get("item")));
  }

  public void trigger(ServerPlayer player, ItemStack crafted) {
    trigger(player, instance -> instance.item.matches(crafted));
  }

  public static Instance craftedItem(ItemLike item) {
    return new Instance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(item).build());
  }

  public static class Instance extends AbstractCriterionTriggerInstance {
    private final ItemPredicate item;

    public Instance(ContextAwarePredicate player, ItemPredicate item) {
      super(ID, player);
      this.item = item;
    }

    @Override
    public JsonObject serializeToJson(SerializationContext context) {
      JsonObject json = super.serializeToJson(context);
      json.add("item", item.serializeToJson());
      return json;
    }
  }
}
