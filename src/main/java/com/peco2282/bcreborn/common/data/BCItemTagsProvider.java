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
package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.common.data.tag.CommonItemTags;
import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BCItemTagsProvider extends ItemTagsProvider {
  public BCItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
    super(p_275343_, p_275729_, p_275322_, modId, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider p_256380_) {
    gearTag();
    chipsetTag();
    fuelBucketTag();

    tag(CommonItemTags.BUILDERS);
    tag(CommonItemTags.CORE)
      .addTag(CommonItemTags.GEAR).add(ItemsCore.LIST.get(), ItemsCore.MAP_LOCATION.get(), ItemsCore.PAINTBRUSH.get(), ItemsCore.WRENCH.get());
    tag(CommonItemTags.ENERGY)
      .addTag(CommonItemTags.FUEL_BUCKET);
    tag(CommonItemTags.FACTORY);
    tag(CommonItemTags.ROBOTICS)
      .add(RoboticsItems.REDSTONE_BOARDS.getAll().stream().map(RegistryObject::get).toArray(Item[]::new))
      .add(RoboticsItems.ROBOT.get(), RoboticsItems.ROBOT_STATION.get());
    tag(CommonItemTags.SILICON)
      .addTag(CommonItemTags.CHIPSET).add(SiliconItems.PACKAGE_ITEM.get());
    tag(CommonItemTags.TRANSPORT);
  }

  private void fuelBucketTag() {
    tag(CommonItemTags.FUEL_BUCKET)
      .add(FluidsEnergy.OIL_BUCKET.get(), FluidsEnergy.FUEL_BUCKET.get());
  }

  private void chipsetTag() {
    tag(CommonItemTags.CHIPSET)
      .add(SiliconItems.REDSTONE_CHIPSET.get(), SiliconItems.IRON_CHIPSET.get(), SiliconItems.GOLD_CHIPSET.get(), SiliconItems.DIAMOND_CHIPSET.get(), SiliconItems.PULSATING_CHIPSET.get(), SiliconItems.QUARTZ_CHIPSET.get(), SiliconItems.COMP_CHIPSET.get(), SiliconItems.EMERALD_CHIPSET.get());
  }

  private void gearTag() {
    tag(CommonItemTags.GEAR)
      .add(ItemsCore.WOODEN_GEAR.get(), ItemsCore.IRON_GEAR.get(), ItemsCore.GOLD_GEAR.get(), ItemsCore.DIAMOND_GEAR.get());
  }
}
