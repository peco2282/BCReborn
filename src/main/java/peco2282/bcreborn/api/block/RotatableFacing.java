package peco2282.bcreborn.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface RotatableFacing extends Facing {
  default boolean isRotatable(Level world, BlockPos pos, BlockState state) {
    return true;
  }

  default InteractionResult rotate(Level world, BlockPos pos, BlockState state, Direction side) {
    if (!isRotatable(world, pos, state)) {
      return InteractionResult.FAIL;
    }
    Direction currentFacing = state.getValue(getFacingProperty());
    Direction newFacing = currentFacing.getClockWise();
    world.setBlock(pos, state.setValue(getFacingProperty(), newFacing), 0);
    return InteractionResult.SUCCESS;
  }
}
