package peco2282.bcreborn.core.block;

import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;
import peco2282.bcreborn.api.enums.EnumDecoratedBlock;

public class BlockDecoration extends BlockBaseNeptune {
  public static final Property<EnumDecoratedBlock> DECORATED_TYPE = BCProperties.DECORATED_BLOCK;

  public BlockDecoration(String id) {
    super(
        Properties.of()
            .lightLevel(state -> {
              EnumDecoratedBlock type = state.getValue(DECORATED_TYPE);
              return type.getLightValue();
            })
            .destroyTime(5.0F)
            .explosionResistance(10.0F)
            .sound(SoundType.METAL)
        ,
        id,
        new Tuple<>(DECORATED_TYPE, EnumDecoratedBlock.DESTROY)
    );
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(DECORATED_TYPE);
  }
}
