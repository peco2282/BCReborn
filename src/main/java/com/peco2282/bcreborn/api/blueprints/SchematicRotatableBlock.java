package com.peco2282.bcreborn.api.blueprints;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class SchematicRotatableBlock extends SchematicBlock {
  public SchematicRotatableBlock(BlockState state) {
    this.block = state.getBlock();
    this.state = state;
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    this.state = this.state.rotate(context.world(), null, Rotation.COUNTERCLOCKWISE_90);
  }
}
