/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.core.fluid.BCCoreFluids;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCCoreItems {
  public static final RegistryObject<WrenchItem> WRENCH =
      register("wrench", () -> new WrenchItem("wrench"));
  public static final RegistryObject<GearItem> GEAR_WOOD =
      register("gear_wood", () -> new GearItem("gear.wood"));
  public static final RegistryObject<GearItem> GEAR_STONE =
      register("gear_stone", () -> new GearItem("gear.stone"));
  public static final RegistryObject<GearItem> GEAR_IRON =
      register("gear_iron", () -> new GearItem("gear.iron"));
  public static final RegistryObject<GearItem> GEAR_GOLD =
      register("gear_gold", () -> new GearItem("gear.gold"));
  public static final RegistryObject<GearItem> GEAR_DIAMOND =
      register("gear_diamond", () -> new GearItem("gear.diamond"));

  public static final RegistryObject<BucketItem> OIL_BUCKET =
      register(
          "bucket_oil",
          () ->
              new BucketItem(
                  BCCoreFluids.OIL_SOURCE,
                  new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
  public static final RegistryObject<BucketItem> FUEL_BUCKET =
      register(
          "bucket_fuel",
          () ->
              new BucketItem(
                  BCCoreFluids.FUEL_SOURCE,
                  new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

  private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item) {
    return BCRegistry.registerItem(name, item);
  }
}
