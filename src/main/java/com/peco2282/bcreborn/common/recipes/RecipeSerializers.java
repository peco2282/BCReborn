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
package com.peco2282.bcreborn.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.IntegrationRecipe;
import com.peco2282.bcreborn.api.recipes.ProgrammingRecipe;
import com.peco2282.bcreborn.api.recipes.RefineryRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

public final class RecipeSerializers {
  private static final Logger LOGGER = BCReborn.createLogger();
  public static class AssemblyRecipeSerializer {
    public static JsonObject toJson(AssemblyRecipe recipe) {
      var json = new JsonObject();
      var list = new JsonArray(recipe.ingredients().size());
      recipe.ingredients().forEach(ingredient -> list.add(ingredient.toJson()));
      json.add("ingredients", list);
      var stack = fromStack(recipe.result());
      json.add("result", stack);
      json.addProperty("energy", recipe.energy());
      json.addProperty("craftingTime", recipe.craftingTime());
      return json;
    }

    public static AssemblyRecipe fromJson(ResourceLocation id, JsonObject json) {
      try {
        var resultStack = toStack(json.get("result"));
        var list = new ArrayList<Ingredient>();
        for (var ingredient : json.getAsJsonArray("ingredients")) {
          list.add(Ingredient.fromJson(ingredient));
        }

        return new AssemblyRecipe(
          id,
          list,
          resultStack,
          json.get("energy").getAsInt(),
          json.get("craftingTime").getAsInt()
        );
      } catch (Exception e) {
        throw new JsonParseException("Failed to parse AssemblyRecipe", e);
      }
    }
  }
  public static class IntegrationRecipeSerializer {
    public static JsonObject toJson(IntegrationRecipe recipe) {
      var json = new JsonObject();

      json.add("input", recipe.input().toJson());
      var expansions = new JsonArray(recipe.expansions().size());
      for (var expansion : recipe.expansions()) {
        expansions.add(expansion.toJson());
      }
      json.add("expansions", expansions);
      var result = fromStack(recipe.result());
      json.add("result", result);

      json.addProperty("energy", recipe.energy());
      json.addProperty("max_expansion_count", recipe.maxExpansionCount());

      return json;
    }
    public static IntegrationRecipe fromJson(ResourceLocation id, JsonObject json) {
      var input = Ingredient.fromJson(json.get("input"));
      var expansions = new ArrayList<Ingredient>();
      for (var expansion : json.get("expansions").getAsJsonArray()) {
        expansions.add(Ingredient.fromJson(expansion));
      }
      var result = toStack(json.get("result"));
      return new IntegrationRecipe(id, input, expansions, result, json.get("energy").getAsInt(), json.get("max_expansion_count").getAsInt());
    }
  }
  public static class ProgrammingRecipeSerializer {
    public static JsonObject toJson(ProgrammingRecipe recipe) {
      var json = new JsonObject();
      json.addProperty("type", "programming");
      json.add("input", recipe.input().toJson());
      json.add("option", recipe.option().toJson());
      json.add("result", fromStack(recipe.result()));
      json.addProperty("energy", recipe.energy());
      return json;
    }
    public static ProgrammingRecipe fromJson(ResourceLocation id, JsonObject json) {
      var input = Ingredient.fromJson(json.get("input"));
      var option = Ingredient.fromJson(json.get("option"));
      var result = toStack(json.get("result"));
      return new ProgrammingRecipe(id, input, option, result, json.get("energy").getAsInt());
    }
  }

  public static class RefineryRecipeSerializer {
    public static JsonObject toJson(RefineryRecipe recipe) {
      var json = new JsonObject();
      json.add("primary", recipe.primary().toJson());
      if (recipe.secondary().isPresent() && recipe.secondary().get() != Ingredient.EMPTY)
        json.add("secondary", recipe.secondary().get().toJson());
      json.add("fluid", fromFluid(recipe.result()));
      json.addProperty("energy", recipe.energy());
      json.addProperty("delay", recipe.delay());
      return json;
    }
    public static RefineryRecipe fromJson(ResourceLocation id, JsonObject json) {
      var primary = Ingredient.fromJson(json.get("primary"));
      var secondary = json.has("secondary") ? Ingredient.fromJson(json.get("secondary")) : Ingredient.EMPTY;
      var result = toFluid(json.get("fluid"));
      return new RefineryRecipe(id, primary, Optional.of(secondary), result, json.get("energy").getAsInt(), json.get("delay").getAsInt());
    }
  }

  private static JsonElement fromStack(ItemStack stack) {
    return ItemStack.CODEC
      .encodeStart(JsonOps.INSTANCE, stack)
      .getOrThrow(false, LOGGER::error);
  }

  private static ItemStack toStack(JsonElement json) {
    return ItemStack.CODEC
      .parse(JsonOps.INSTANCE, json)
      .getOrThrow(false, LOGGER::error);
  }
  private static JsonElement fromFluid(FluidStack stack) {
    return FluidStack.CODEC
      .encodeStart(JsonOps.INSTANCE, stack)
      .getOrThrow(false, LOGGER::error);
  }

  private static FluidStack toFluid(JsonElement json) {
    return FluidStack.CODEC
      .parse(JsonOps.INSTANCE, json)
      .getOrThrow(false, LOGGER::error);
  }
}
