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
package com.peco2282.bcreborn.factory.data;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.data.BCBlockStateHelper;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import com.peco2282.bcreborn.factory.block.TankBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FactoryBlockStateProvider extends BCBlockStateHelper {
  public FactoryBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornFactory.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlockWithItem(
      FactoryBlocks.AUTO_WORKBENCH.get(),
      models().cubeBottomTop(
        "auto_workbench_block",
        modLoc("block/auto_workbench_block/side"),
        modLoc("block/auto_workbench_block/bottom"),
        modLoc("block/auto_workbench_block/top")
      )
    );

    simpleBlockWithItem(
      FactoryBlocks.FLOOD_GATE.get(),
      models().cubeBottomTop(
        "flood_gate_block",
        modLoc("block/flood_gate_block/side"),
        modLoc("block/flood_gate_block/bottom"),
        modLoc("block/flood_gate_block/top")
      )
    );

    simpleBlockWithItem(FactoryBlocks.MINING_WELL.get(), models().getBuilder("mining_well")
      .texture("back", modLoc("block/mining_well_block/back"))
      .texture("front", modLoc("block/mining_well_block/front"))
      .texture("side", modLoc("block/mining_well_block/side"))
      .texture("top", modLoc("block/mining_well_block/top"))
      .texture("bottom", modLoc("block/mining_well_block/bottom"))
      .element()
      .from(0, 0, 0).to(16, 16, 16)
      .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#front").end()
      .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#side").end()
      .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#back").end()
      .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#side").end()
      .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end()
      .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").end()
      .end()
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.GUI).rotation(22.5F, 135, 0).scale(.6F, .6F, .6F).end()
      .end()
    );

    simpleBlockWithItem(
      FactoryBlocks.PUMP.get(),
      models().cubeBottomTop(
        "pump",
        modLoc("block/pump_block/side"),
        modLoc("block/pump_block/bottom"),
        modLoc("block/pump_block/top")
      )
    );

    var tank = models().getBuilder("tank")
      .texture("side", modLoc("block/tank_block/side"))
      .texture("topbottom", modLoc("block/tank_block/topbottom"))
      .renderType("cutout")
      .element()
      .from(2, 0, 2).to(14, 16, 14)
      .face(Direction.NORTH).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.EAST).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.SOUTH).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.WEST).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.UP).uvs(2, 2, 14, 14).texture("#topbottom").end()
      .face(Direction.DOWN).uvs(2, 2, 14, 14).texture("#topbottom").end()
      .end()
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.GUI).rotation(22.5F, 45, 0).scale(.6F, .6F, .6F).end()
      .end();

    var tankStacked = models().getBuilder("tank_stacked")
      .texture("side", modLoc("block/tank_block/side"))
      .texture("topbottom", modLoc("block/tank_block/topbottom"))
      .renderType("cutout")
      .element()
      .from(2, 0, 2).to(14, 16, 14)
      .face(Direction.NORTH).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.EAST).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.SOUTH).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.WEST).uvs(2, 0, 14, 16).texture("#side").end()
      .face(Direction.UP).uvs(2, 2, 14, 14).texture("#topbottom").end()
      .face(Direction.DOWN).uvs(2, 2, 14, 14).texture("#topbottom").end()
      .end()
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(.3F, .3F, .3F).end()
      .transform(ItemDisplayContext.GUI).rotation(22.5F, 45, 0).scale(.6F, .6F, .6F).end()
      .end();

    getVariantBuilder(FactoryBlocks.TANK.get())
      .partialState()
      .with(TankBlock.IS_STACKED, false)
      .modelForState()
      .modelFile(tank)
      .addModel()

      .partialState()
      .with(TankBlock.IS_STACKED, true)
      .modelForState()
      .modelFile(tankStacked)
      .addModel();

    simpleBlockItem(FactoryBlocks.TANK.get(), tank);


    simpleBlockItem(FactoryBlocks.REFINERY.get(), models().getBuilder("refinery_inventory")
      .texture("refinery", modLoc("block/refinery_block/refinery"))

      .element()
      .from(0, 0, 0).to(8, 16, 8)
      .face(Direction.NORTH).uvs(0, 4, 2, 12).texture("#refinery").end()
      .face(Direction.EAST).uvs(2, 4, 4, 12).texture("#refinery").end()
      .face(Direction.SOUTH).uvs(4, 4, 6, 12).texture("#refinery").end()
      .face(Direction.WEST).uvs(6, 4, 8, 12).texture("#refinery").end()
      .face(Direction.UP).uvs(2, 0, 4, 4).texture("#refinery").end()
      .face(Direction.DOWN).uvs(4, 0, 6, 3.75F).texture("#refinery").end()
      .end()

      .element()
      .from(8, 0, 0).to(16, 16, 8)
      .face(Direction.NORTH).uvs(0, 4, 2, 12).texture("#refinery").end()
      .face(Direction.EAST).uvs(2, 4, 4, 12).texture("#refinery").end()
      .face(Direction.SOUTH).uvs(4, 4, 6, 12).texture("#refinery").end()
      .face(Direction.WEST).uvs(6, 4, 8, 12).texture("#refinery").end()
      .face(Direction.UP).uvs(2, 0, 4, 4).texture("#refinery").end()
      .face(Direction.DOWN).uvs(4, 0, 6, 3.75F).texture("#refinery").end()
      .end()

      .element()
      .from(4, 0, 8).to(12, 16, 16)
      .face(Direction.NORTH).uvs(0, 4, 2, 12).texture("#refinery").end()
      .face(Direction.EAST).uvs(2, 4, 4, 12).texture("#refinery").end()
      .face(Direction.SOUTH).uvs(4, 4, 6, 12).texture("#refinery").end()
      .face(Direction.WEST).uvs(6, 4, 8, 12).texture("#refinery").end()
      .face(Direction.UP).uvs(2, 0, 4, 4).texture("#refinery").end()
      .face(Direction.DOWN).uvs(4, 0, 6, 3.75F).texture("#refinery").end()
      .end()

      .element()
      .from(0, 0, 8).to(4, 4, 16)
      .face(Direction.NORTH).uvs(8, 14, 9, 16).texture("#refinery").end()
      .face(Direction.EAST).uvs(9, 14, 11, 16).texture("#refinery").end()
      .face(Direction.SOUTH).uvs(11, 14, 12, 16).texture("#refinery").end()
      .face(Direction.WEST).uvs(12, 14, 14, 16).texture("#refinery").end()
      .face(Direction.UP).uvs(9, 12, 11, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#refinery").end()
      .face(Direction.DOWN).uvs(11, 12, 13, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#refinery").end()
      .end()

      .element()
      .from(12, 0, 8).to(16, 4, 16)
      .face(Direction.NORTH).uvs(8, 14, 9, 16).texture("#refinery").end()
      .face(Direction.EAST).uvs(9, 14, 11, 16).texture("#refinery").end()
      .face(Direction.SOUTH).uvs(11, 14, 12, 16).texture("#refinery").end()
      .face(Direction.WEST).uvs(12, 14, 14, 16).texture("#refinery").end()
      .face(Direction.UP).uvs(9, 12, 11, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#refinery").end()
      .face(Direction.DOWN).uvs(11, 12, 13, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#refinery").end()
      .end()

      .transforms()
      .transform(ItemDisplayContext.GUI)
      .rotation(22.5F, 45F, 0F)
      .translation(0.25F, 0F, 0F)
      .scale(0.6F)
      .end()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
      .scale(0.3F)
      .end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
      .scale(0.3F)
      .end()
      .end()
    );
  }
}
