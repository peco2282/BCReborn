/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.lib.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import peco2282.bcreborn.BCReborn;

/**
 * Contains definitions and registration for custom damage types used in the BCReborn mod.
 * Specifically defines damage types for "oil" and "fuel".
 *
 * @author peco2282
 */
public class LiquidDamageType {
  /** A damage type representing exposure to oil. Causes minimal damage over time. */
  public static final ResourceKey<DamageType> OIL_EX =
      ResourceKey.create(Registries.DAMAGE_TYPE, BCReborn.location("oil"));

  /** A damage type representing exposure to fuel. Causes moderate damage over time. */
  public static final ResourceKey<DamageType> FUEL_EX =
      ResourceKey.create(Registries.DAMAGE_TYPE, BCReborn.location("fuel"));

  /**
   * Registers custom damage types with their respective behaviors into the given context.
   *
   * @param context The bootstrap context for registering damage types.
   */
  public static void bootstrap(BootstrapContext<DamageType> context) {
    context.register(OIL_EX, new DamageType("oil", .2F));
    context.register(FUEL_EX, new DamageType("fuel", .5F));
  }
}
