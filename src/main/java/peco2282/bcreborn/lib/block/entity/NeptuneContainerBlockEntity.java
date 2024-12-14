package peco2282.bcreborn.lib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCBlockEntity;


/**
 * Represents an abstract container block entity specific to the Neptune system.
 * This class extends {@link BaseContainerBlockEntity} and implements {@link BCBlockEntity}.
 *
 * @author peco2282
 */
public abstract class NeptuneContainerBlockEntity extends BaseContainerBlockEntity implements BCBlockEntity {
  /**
   * Constructs a new NeptuneContainerBlockEntity.
   *
   * @param type  The type of the block entity.
   * @param pos   The position of the block in the world.
   * @param state The current state of the block.
   */
  protected NeptuneContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }
}
