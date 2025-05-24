/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.lib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.lib.block.entity.BCBaseBlockEntity;
import peco2282.bcreborn.utils.PropertyBuilder;

import java.util.function.BiFunction;
import javax.annotation.Nullable;

public abstract class BCBaseEntityBlock extends BCBaseBlock implements EntityBlock, BCBlock {
  /**
   * Constructs a new instance of BCBaseEntityBlock with specified properties, ID, and a property
   * builder.
   *
   * @param properties the properties of the block
   * @param id the unique identifier for the block
   * @param builder the property builder for additional block configuration
   */
  public BCBaseEntityBlock(Properties properties, @NotNull String id, PropertyBuilder builder) {
    super(properties, id, builder);
  }

  /**
   * Constructs a new instance of BCBaseEntityBlock with specified properties and ID.
   *
   * @param properties the properties of the block
   * @param id the unique identifier for the block
   */
  public BCBaseEntityBlock(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder());
  }

  /**
   * Creates and returns a {@link MapCodec} instance for encoding a BCBaseEntityBlock.
   *
   * @param function the bi-function to create a new BCBaseEntityBlock instance
   * @param <T> the type of the block
   * @return a codec for encoding properties and ID of a BCBaseEntityBlock
   */
  protected static <T extends BCBaseEntityBlock> MapCodec<T> codecInstance(
      BiFunction<Properties, String, T> function) {
    return RecordCodecBuilder.mapCodec(
        instance ->
            instance
                .group(propertiesCodec(), Codec.STRING.fieldOf("id").forGetter(BCBaseBlock::getId))
                .apply(instance, function));
  }

  /**
   * Creates and returns a new block entity at the given position with the given state.
   *
   * @param pos the position of the block
   * @param state the block state
   * @return a new block entity for the block
   */
  @NotNull
  @Override
  public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

  @Override
  protected abstract @NotNull MapCodec<? extends BCBaseEntityBlock> codec();

  /**
   * Gathers the state properties for this block and defines any additional custom state pipe.
   *
   * @param builder the state definition builder to configure with the block's state properties
   */
  protected abstract void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

  /**
   * Sets up the state definition for this block by invoking {@link
   * #gatherStateDefinition(StateDefinition.Builder)}.
   *
   * @param p_49915_ the state definition builder used to configure the block's state properties
   */
  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    gatherStateDefinition(p_49915_);
  }

  @Override
  public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    BlockEntity entity = level.getBlockEntity(pos);
    if (entity instanceof BCBaseBlockEntity nep) {
      nep.onExplode(explosion);
    }
    super.onBlockExploded(state, level, pos, explosion);
  }

  /**
   * Handles the removal of this block from the world by performing cleanup on associated block
   * entities.
   *
   * @param p_60515_ the old block state
   * @param p_60516_ the level in which the block exists
   * @param p_60517_ the position of the block
   * @param p_60518_ the new block state replacing the old one
   * @param p_60519_ whether the block was removed naturally or via a player action
   */
  @Override
  protected void onRemove(
      BlockState p_60515_,
      Level p_60516_,
      BlockPos p_60517_,
      BlockState p_60518_,
      boolean p_60519_) {
    if (p_60516_.getBlockEntity(p_60517_) instanceof BCBaseBlockEntity nep) {
      nep.onRemove();
    }
    super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
  }

  /**
   * Retrieves the server-side {@link BlockEntityTicker} for the specified block entity type.
   *
   * @param type the block entity type
   * @param <E> the type parameter for the block entity
   * @return the server-side ticker for the block entity; otherwise null
   */
  /**
   * Provides a server-side ticker for this block's corresponding block entity type.
   *
   * @param type the block entity type for which the ticker is required
   * @param <E> the type of block entity being processed
   * @return a {@link BlockEntityTicker} for server-side operations, or null if not applicable
   */
  @Nullable
  protected abstract <E extends BlockEntity> BlockEntityTicker<E> serverTicker(
      BlockEntityType<E> type);

  @Override
  /**
   * Retrieves the appropriate {@link BlockEntityTicker} based on the level and block entity type.
   *
   * @param level the level in which the block exists
   * @param state the current state of the block
   * @param type the type of the block entity
   * @param <T> the type parameter of the block entity
   * @return a BlockEntityTicker for server-side operations or null for client-side
   */
  public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide() ? null : serverTicker(type);
  }

  /**
   * Helper method to create a {@link BlockEntityTicker} if the types match.
   *
   * @param be1 the first block entity type
   * @param be2 the second block entity type
   * @param ticker the ticker instance to use if types match
   * @param <E> the generic type of the original block entity
   * @param <A> the generic type of the desired block entity
   * @return a BlockEntityTicker if the types match; otherwise null
   */
  /**
   * Creates a {@link BlockEntityTicker} for a block entity type if the provided types match.
   *
   * @param be1 the target block entity type to compare against
   * @param be2 the block entity type that needs to be checked
   * @param ticker the ticker to associate if the block entity types match
   * @param <E> the general type of the source block entity
   * @param <A> the type of the target block entity
   * @return a {@link BlockEntityTicker} if the block entity types match, or null otherwise
   */
  @Nullable
  @SuppressWarnings("unchecked")
  protected static <E extends BlockEntity, A extends BlockEntity>
      BlockEntityTicker<A> createTickerHelper(
          BlockEntityType<A> be1, BlockEntityType<E> be2, BlockEntityTicker<? super E> ticker) {
    return be2 == be1 ? (BlockEntityTicker<A>) ticker : null;
  }
}
