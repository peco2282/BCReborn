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
package com.peco2282.bcreborn.robotics.data;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.data.BCBlockStateHelper;
import com.peco2282.bcreborn.robotics.RoboticsBlocks;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RoboticsBlockStateProvider extends BCBlockStateHelper {
  public RoboticsBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, BCRebornRobotics.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    simpleBlockWithItem(RoboticsBlocks.REQUESTER.get(), models()
      .getBuilder("requester")
      .texture("back", modLoc("block/requester/back"))
      .texture("bottom", modLoc("block/requester/bottom"))
      .texture("front", modLoc("block/requester/front"))
      .texture("side", modLoc("block/requester/side"))
      .texture("top", modLoc("block/requester/top"))
      .element()
      .from(0, 0, 0)
      .to(16, 16, 16)
      .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#front").end()
      .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#side").end()
      .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#back").end()
      .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#side").end()
      .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").end()
      .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").end()
      .end()
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(0.3F).end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(0.3F).end()
      .transform(ItemDisplayContext.GUI).rotation(22.5F, 135, 0).scale(0.6F).end()
      .end()
    );

    simpleBlockWithItem(RoboticsBlocks.ZONE_PLAN.get(), models()
      .getBuilder("zone_plan")
      .texture("back", modLoc("block/zone_plan/back"))
      .texture("default", modLoc("block/zone_plan/default"))
      .texture("front", modLoc("block/zone_plan/front"))
      .texture("left", modLoc("block/zone_plan/left"))
      .texture("right", modLoc("block/zone_plan/right"))
      .texture("top", modLoc("block/zone_plan/top"))
      .element()
      .from(0, 0, 0)
      .to(16, 16, 16)
      .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#front").end()
      .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#left").end()
      .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#back").end()
      .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#right").end()
      .face(Direction.UP).uvs(0, 0, 16, 16).rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).texture("#top").end()
      .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#default").end()
      .end()
      .transforms()
      .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).scale(0.3F).end()
      .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).scale(0.3F).end()
      .transform(ItemDisplayContext.GUI).rotation(22.5F, 135, 0).scale(0.6F).end()
      .end());

    RoboticsItems.REDSTONE_BOARDS.getMap().forEach((k, v) -> {
      itemModels()
        .getBuilder(v.getId().getPath())
        .parent(getExistingFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/board/" + k));
    });

    itemModels().getBuilder("robot")
      .parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));

    itemModels().getBuilder("robot_station")
      .parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
  }
}
