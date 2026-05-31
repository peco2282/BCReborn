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

import com.peco2282.bcreborn.BCRebornCore;
import net.minecraft.world.item.DyeColor;
import com.peco2282.bcreborn.core.ItemsCore;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CoreItemModelProvider extends ItemModelProvider {
  public CoreItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, BCRebornCore.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(ItemsCore.WOODEN_GEAR.get());
    basicItem(ItemsCore.STONE_GEAR.get());
    basicItem(ItemsCore.IRON_GEAR.get());
    basicItem(ItemsCore.GOLD_GEAR.get());
    basicItem(ItemsCore.DIAMOND_GEAR.get());

    getBuilder("list")
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", "item/list/clean")
            .override()
            .predicate(modLoc("written"), 1.0f)
            .model(getBuilder("list_used")
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", "item/list/used"))
            .end();

    // basicItem(ItemsCore.LIST.get()); // Removed as it was causing the error and replaced with the builder above

    var paintbrushBuilder = getBuilder("paintbrush")
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", "item/paintbrush/clean");

    for (int i = 0; i < 16; i++) {
        DyeColor color = DyeColor.byId(i);
        paintbrushBuilder.override()
                .predicate(modLoc("color"), i + 1)
                .model(getBuilder("paintbrush_" + color.getName())
                        .parent(getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", "item/paintbrush/" + color.getName()))
                .end();
    }

    getBuilder("map_location")
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", "item/map_location/clean")
            .override().predicate(modLoc("kind"), 1.0f).model(getBuilder("map_location_spot").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/spot")).end()
            .override().predicate(modLoc("kind"), 2.0f).model(getBuilder("map_location_area").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/area")).end()
            .override().predicate(modLoc("kind"), 3.0f).model(getBuilder("map_location_path").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/path")).end()
            .override().predicate(modLoc("kind"), 4.0f).model(getBuilder("map_location_zone").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/zone")).end();


    withExistingParent("wrench", mcLoc("item/generated"))
        .texture("layer0", modLoc("item/wrench_item"));
    getBuilder("wood_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
//    withExistingParent("wood_engine", mcLoc("builtin/entity"));
  }
}
