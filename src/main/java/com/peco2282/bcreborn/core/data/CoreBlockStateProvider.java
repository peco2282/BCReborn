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
import com.peco2282.bcreborn.common.block.MarkerBlock;
import com.peco2282.bcreborn.common.data.BCBlockStateHelper;
import com.peco2282.bcreborn.core.BlocksCore;
import com.peco2282.bcreborn.core.ItemsCore;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CoreBlockStateProvider extends BCBlockStateHelper {
  public CoreBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornCore.MODID, exFileHelper);
  }

  @SuppressWarnings("deprecation")
  private static String getName(Block block) {
    return block.builtInRegistryHolder().key().location().getPath();
  }

  @Override
  protected void registerStatesAndModels() {
    simpleEngine(BlocksCore.WOODEN_ENGINE.get());
//    simpleBlockWithItem(BlocksCore.BLUE_MARKER.get(), models().withExistingItemParent("blue_marker", mcLoc("block/template_torch")).texture("torch", "block/marker_block/default").renderType(mcLoc("cutout")));
    ModelFile model = models()
      .getBuilder("blue_marker")
      .texture("torch", modLoc("block/marker_block/default"))
      .renderType("cutout")
      .ao(false)

      // 芯
      .element()
      .from(7, 0, 7)
      .to(9, 10, 9)
      .shade(false)
      .face(Direction.UP)
      .uvs(7, 6, 9, 8)
      .texture("#torch")
      .end()
      .face(Direction.DOWN)
      .uvs(7, 13, 9, 15)
      .texture("#torch")
      .end()
      .end()

      // X面
      .element()
      .from(7, 0, 0)
      .to(9, 16, 16)
      .shade(false)
      .face(Direction.WEST)
      .uvs(0, 0, 16, 16)
      .texture("#torch")
      .end()
      .face(Direction.EAST)
      .uvs(0, 0, 16, 16)
      .texture("#torch")
      .end()
      .end()

      // Z面
      .element()
      .from(0, 0, 7)
      .to(16, 16, 9)
      .shade(false)
      .face(Direction.NORTH)
      .uvs(0, 0, 16, 16)
      .texture("#torch")
      .end()
      .face(Direction.SOUTH)
      .uvs(0, 0, 16, 16)
      .texture("#torch")
      .end()
      .end();
    getVariantBuilder(BlocksCore.BLUE_MARKER.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(MarkerBlock.FACING);

        int xRot = switch (dir) {
          case DOWN -> 180;
          case NORTH -> 90;
          case SOUTH -> 270;
          default -> 0;
        };

        int yRot = switch (dir) {
          case EAST -> 90;
          case SOUTH -> 180;
          case WEST -> 270;
          default -> 0;
        };

        return ConfiguredModel.builder()
          .modelFile(model)
          .rotationX(xRot)
          .rotationY(yRot)
          .build();
      });

    itemModels().withExistingParent(getName(BlocksCore.BLUE_MARKER.get()), mcLoc("item/generated"));

    // Item models

    basicItem(ItemsCore.WOODEN_GEAR.get());
    basicItem(ItemsCore.STONE_GEAR.get());
    basicItem(ItemsCore.IRON_GEAR.get());
    basicItem(ItemsCore.GOLD_GEAR.get());
    basicItem(ItemsCore.DIAMOND_GEAR.get());

    getItemBuilder("list")
      .parent(getExistingFile(mcLoc("item/generated")))
      .texture("layer0", "item/list/clean")
      .override()
      .predicate(modLoc("written"), 1.0f)
      .model(getItemBuilder("list_used")
        .parent(getExistingFile(mcLoc("item/generated")))
        .texture("layer0", "item/list/used"))
      .end();

    // basicItem(ItemsCore.LIST.get()); // Removed as it was causing the error and replaced with the builder above

    var paintbrushBuilder = getItemBuilder("paintbrush")
      .parent(getExistingFile(mcLoc("item/generated")))
      .texture("layer0", "item/paintbrush/clean");

    for (DyeColor color : DyeColor.values()) {
      paintbrushBuilder.override()
        .predicate(modLoc("color"), color.getId() + 1)
        .model(getItemBuilder("paintbrush_" + color.getName())
          .parent(getExistingFile(mcLoc("item/generated")))
          .texture("layer0", "item/paintbrush/" + color.getName()))
        .end();
    }

    itemModels().getBuilder("map_location")
      .parent(getExistingFile(mcLoc("item/generated")))
      .texture("layer0", "item/map_location/clean")
      .override().predicate(modLoc("kind"), 1.0f).model(getItemBuilder("map_location_spot").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/spot")).end()
      .override().predicate(modLoc("kind"), 2.0f).model(getItemBuilder("map_location_area").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/area")).end()
      .override().predicate(modLoc("kind"), 3.0f).model(getItemBuilder("map_location_path").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/path")).end()
      .override().predicate(modLoc("kind"), 4.0f).model(getItemBuilder("map_location_zone").parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/map_location/zone")).end();


    withExistingItemParent("wrench", mcLoc("item/generated"))
      .texture("layer0", modLoc("item/wrench_item"));
    getItemBuilder("wood_engine").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
//    withExistingItemParent("wood_engine", mcLoc("builtin/entity"));
  }

  private void simpleEngine(Block block) {
    // BERで描画するため builtin/entity モデルを使用
    ModelFile model = new ModelFile.UncheckedModelFile(mcLoc("builtin/entity"));
    simpleBlock(block, model);
    itemModels()
      .getBuilder(getName(block))
      .parent(model)
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(0, 3, -2).scale(0.23F, 0.23F, 0.23F).end()
      .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(30, 160, 0).translation(0, 3, 0).scale(0.375F, 0.375F, 0.375F).end()
      .transform(ItemDisplayContext.GUI).rotation(30, 160, 0).translation(2, 3, 0).scale(0.5325F, 0.5325F, 0.5325F).end()
      .end();
//    itemModels().withExistingParent(getName(block), ResourceLocation.withDefaultNamespace("item/generated"));
  }

  private ModelFile cubeAllWithBaseTex(Block block, String texturePath) {
    ResourceLocation tex = modLoc("block/" + texturePath);
    String modelName = getName(block);
    var all = models().cubeAll(modelName, tex);
    itemModels().withExistingParent(modelName, all.getLocation());
    return all;
  }

}
