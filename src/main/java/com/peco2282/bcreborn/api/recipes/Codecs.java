package com.peco2282.bcreborn.api.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.Ingredient;

public class Codecs {
  public static final Codec<Ingredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.xmap(
    dynamic -> Ingredient.fromJson(
      dynamic.convert(JsonOps.INSTANCE).getValue()
    ),
    ingredient -> new Dynamic<>(
      JsonOps.INSTANCE,
      ingredient.toJson()
    )
  );
}
