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
package com.peco2282.bcreborn.builders.data;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.block.*;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BuildersBlockStateProvider extends BlockStateProvider {
  public BuildersBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornBuilders.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    ResourceLocation architect = models()
      .getBuilder("architect")
      .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
      .texture("bottom", createTexture("architect", "bottom"))
      .texture("front", createTexture("architect", "front"))
      .texture("left", createTexture("architect", "left"))
      .texture("right", createTexture("architect", "right"))
      .texture("back", createTexture("architect", "back"))
      .texture("top", createTexture("architect", "top"))
      .getLocation();

    ResourceLocation builder = models()
      .getBuilder("builder")
      .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
      .texture("bottom", createTexture("builder", "bottom"))
      .texture("front", createTexture("builder", "front"))
      .texture("left", createTexture("builder", "side"))
      .texture("right", createTexture("builder", "side"))
      .texture("back", createTexture("builder", "back"))
      .texture("top", createTexture("builder", "top"))
      .getLocation();

    ResourceLocation filler = models()
      .getBuilder("filler")
      .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
      .texture("bottom", createTexture("filler", "bottom"))
      .texture("front", createTexture("filler", "front"))
      .texture("left", createTexture("filler", "side"))
      .texture("right", createTexture("filler", "side"))
      .texture("back", createTexture("filler", "side"))
      .texture("top", createTexture("filler", "top"))
      .getLocation();

    ResourceLocation library = models()
      .getBuilder("library")
      .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
      .texture("bottom", createTexture("library", "bottom"))
      .texture("front", createTexture("library", "front"))
      .texture("left", createTexture("library", "left"))
      .texture("right", createTexture("library", "right"))
      .texture("back", createTexture("library", "back"))
      .texture("top", createTexture("library", "top"))
      .getLocation();


    ResourceLocation quarry = models()
      .getBuilder("quarry")
      .parent(models().getExistingFile(BCReborn.getBasedLocation("block/template_machine")))
      .texture("bottom", createTexture("quarry", "bottom"))
      .texture("front", createTexture("quarry", "front"))
      .texture("left", createTexture("quarry", "side"))
      .texture("right", createTexture("quarry", "side"))
      .texture("back", createTexture("quarry", "back"))
      .texture("top", createTexture("quarry", "top"))
      .getLocation();

    getVariantBuilder(BuildersBlock.ARCHITECT.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(ArchitectBlock.HORIZONTAL_FACING);
        return ConfiguredModel.builder()
          .modelFile(models().getExistingFile(architect))
          .rotationX(0)
          .rotationY((((int) dir.toYRot()) + 180) % 360)
          .build();
      });
    getVariantBuilder(BuildersBlock.BUILDER.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(BuilderBlock.HORIZONTAL_FACING);
        return ConfiguredModel.builder()
          .modelFile(models().getExistingFile(builder))
          .rotationX(0)
          .rotationY((((int) dir.toYRot()) + 180) % 360)
          .build();
      });
    getVariantBuilder(BuildersBlock.FILLER.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(FillerBlock.HORIZONTAL_FACING);
        return ConfiguredModel.builder()
          .modelFile(models().getExistingFile(filler))
          .rotationX(0)
          .rotationY((((int) dir.toYRot()) + 180) % 360)
          .build();
      });
    getVariantBuilder(BuildersBlock.BLUEPRINT_LIBRARY.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(BlueprintLibraryBlock.HORIZONTAL_FACING);
        return ConfiguredModel.builder()
          .modelFile(models().getExistingFile(library))
          .rotationX(dir == Direction.DOWN ? 90 : dir == Direction.UP ? -90 : 0)
          .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
          .build();
      });
    getVariantBuilder(BuildersBlock.QUARRY.get())
      .forAllStates(state -> {
        Direction dir = state.getValue(QuarryBlock.HORIZONTAL_FACING);
        return ConfiguredModel.builder()
          .modelFile(models().getExistingFile(quarry))
          .rotationX(0)
          .rotationY((((int) dir.toYRot()) + 180) % 360)
          .build();
      });

    simpleBlockItem(BuildersBlock.ARCHITECT.get(), models().getExistingFile(architect));
    simpleBlockItem(BuildersBlock.BUILDER.get(), models().getExistingFile(builder));
    simpleBlockItem(BuildersBlock.FILLER.get(), models().getExistingFile(filler));
    simpleBlockItem(BuildersBlock.BLUEPRINT_LIBRARY.get(), models().getExistingFile(library));
    simpleBlockItem(BuildersBlock.QUARRY.get(), models().getExistingFile(quarry));

    simpleBlockWithItem(BuildersBlock.CONSTRUCTION_MARKER.get(), models().withExistingParent("construction_marker", mcLoc("block/template_torch")).texture("torch", "block/construction_marker_block/default").renderType(mcLoc("cutout")));
    ResourceLocation frameTex = createTexture("frame", "default");
    BlockModelBuilder frameCenterModel = models().withExistingParent("block/frame_center", mcLoc("block/block"))
      .renderType(mcLoc("cutout"))
      .element()
      .from(4, 4, 4)
      .to(12, 12, 12)
      .allFaces((direction, faceBuilder) -> faceBuilder.texture("#texture").uvs(4, 4, 12, 12))
      .end()
      .texture("texture", frameTex)
      .texture("particle", frameTex);

    MultiPartBlockStateBuilder frameBuilder = getMultipartBuilder(BuildersBlock.FRAME.get());
    frameBuilder.part().modelFile(frameCenterModel).addModel();

    for (Direction side : Direction.values()) {
      float[] from = getSideFrom(side);
      float[] to = getSideTo(side);

      BlockModelBuilder sideModel = models().withExistingParent("block/frame_" + side.getName(), mcLoc("block/block"))
        .renderType(mcLoc("cutout"))
        .texture("texture", frameTex)
        .texture("particle", frameTex);

      var element = sideModel.element()
        .from(from[0], from[1], from[2])
        .to(to[0], to[1], to[2]);

      for (Direction dir : Direction.values()) {
        float[] uv = getSideUV(side, dir);
        element.face(dir).texture("#texture").uvs(uv[0], uv[1], uv[2], uv[3]).end();
      }
      element.end();

      frameBuilder.part()
        .modelFile(sideModel)
        .addModel()
        .condition(FrameBlock.PROPERTY_MAP.get(side), true);
    }

    simpleBlockItem(BuildersBlock.FRAME.get(), frameCenterModel);
  }

  private float[] getSideFrom(Direction side) {
    return switch (side) {
      case NORTH -> new float[]{4, 4, 0};
      case SOUTH -> new float[]{4, 4, 12};
      case WEST -> new float[]{0, 4, 4};
      case EAST -> new float[]{12, 4, 4};
      case DOWN -> new float[]{4, 0, 4};
      case UP -> new float[]{4, 12, 4};
    };
  }

  private float[] getSideTo(Direction side) {
    return switch (side) {
      case NORTH -> new float[]{12, 12, 4};
      case SOUTH -> new float[]{12, 12, 16};
      case WEST -> new float[]{4, 12, 12};
      case EAST -> new float[]{16, 12, 12};
      case DOWN -> new float[]{12, 4, 12};
      case UP -> new float[]{12, 16, 12};
    };
  }

  private float[] getSideUV(Direction pipeDir, Direction face) {
    if (pipeDir == face || pipeDir == face.getOpposite()) {
      return new float[]{4, 4, 12, 12};
    }
    return switch (pipeDir) {
      case NORTH, SOUTH -> new float[]{4, 0, 12, 4};
      case WEST, EAST -> new float[]{0, 4, 4, 12};
      case DOWN, UP -> new float[]{4, 0, 12, 4};
    };
  }

  private ModelFile.UncheckedModelFile unCheckedModel(String name) {
    return new ModelFile.UncheckedModelFile(modLoc("block/" + name));
  }

  private ModelFile.UncheckedModelFile unCheckedModel(ResourceLocation location) {
    return new ModelFile.UncheckedModelFile(location);
  }

  private ResourceLocation createTexture(String dir, String path) {
    return BCRebornBuilders.location("block/" + dir + "_block/" + path);
  }
}
