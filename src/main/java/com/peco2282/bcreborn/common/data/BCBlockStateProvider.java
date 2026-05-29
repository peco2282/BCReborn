package com.peco2282.bcreborn.common.data;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BCBlockStateProvider extends BlockStateProvider {
  public BCBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
    super(output, modid, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    models()
        .getBuilder("template_machine")
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .face(Direction.NORTH).texture("#front").uvs(0, 0, 16, 16).end()
        .face(Direction.SOUTH).texture("#back").uvs(0, 0, 16, 16).end()
        .face(Direction.EAST).texture("#left").uvs(0, 0, 16, 16).end()
        .face(Direction.WEST).texture("#right").uvs(0, 0, 16, 16).end()
        .face(Direction.UP).texture("#top").uvs(0, 0, 16, 16).end()
        .face(Direction.DOWN).texture("#bottom").uvs(0, 0, 16, 16).end()
        .end()
        .transforms()
        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0, 180, 0).scale(.3F, .3F, .3F).end()
        .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0, 180, 0).scale(.3F, .3F, .3F).end()
        .transform(ItemDisplayContext.GROUND).scale(.3F, .3F, .3F).end()
        .transform(ItemDisplayContext.GUI).rotation(25, 135, 0).scale(.65F, .65F, .65F).translation(-.5F, -.25F, 0).end()
        .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).end()
        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).end()
        .transform(ItemDisplayContext.HEAD).end()
        .transform(ItemDisplayContext.FIXED).end()
        .end()
        ;
  }

}
