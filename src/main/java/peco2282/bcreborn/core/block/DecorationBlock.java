/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumDecoratedBlock;
import peco2282.bcreborn.lib.block.BCBaseBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class DecorationBlock extends BCBaseBlock {
  public static final Property<EnumDecoratedBlock> DECORATED_TYPE = BCProperties.DECORATED_BLOCK;

  public DecorationBlock(String id) {
    super(
        Properties.of()
            .lightLevel(
                state -> {
                  EnumDecoratedBlock type = state.getValue(DECORATED_TYPE);
                  return type.getLightValue();
                })
            .destroyTime(5.0F)
            .explosionResistance(10.0F)
            .sound(SoundType.METAL),
        id,
        PropertyBuilder.builder().add(DECORATED_TYPE, EnumDecoratedBlock.DESTROY));
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(DECORATED_TYPE);
  }
}
