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
import com.peco2282.bcreborn.core.BlocksCore;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CoreBlockStateProvider extends BlockStateProvider {
  public CoreBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornCore.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleEngine(BlocksCore.WOODEN_ENGINE.get());
//    simpleBlockWithItem(BlocksCore.BLUE_MARKER.get(), models().withExistingParent("blue_marker", mcLoc("block/template_torch")).texture("torch", "block/marker_block/default").renderType(mcLoc("cutout")));
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

  @SuppressWarnings("deprecation")
  private static String getName(Block block) {
    return block.builtInRegistryHolder().key().location().getPath();
  }

}
