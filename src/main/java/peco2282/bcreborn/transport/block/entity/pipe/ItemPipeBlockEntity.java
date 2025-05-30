/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.PipeItemBlock;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.PipeType;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;
import peco2282.bcreborn.transport.block.pipe.PipeStorage;
import peco2282.bcreborn.utils.BlockUtil;

import java.util.EnumSet;
import java.util.Map;

public abstract class ItemPipeBlockEntity extends BasePipeBlockEntity {
  protected final EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);

  public ItemPipeBlockEntity(
      BlockEntityType<?> p_155228_,
      BlockPos p_155229_,
      BlockState p_155230_,
      PipeMaterial material) {
    super(p_155228_, p_155229_, p_155230_, material, PipeType.ITEM);
  }

  protected static Map<BlockPos, PipeItemBlock> getNearItemPipe(Level world, BlockPos pos) {
    return BlockUtil.getInstanceBlocks(world, pos, PipeItemBlock.class);
  }

  @Override
  public PipeStorage.ItemStorage getStorage() {
    return this.storage.asItemStorage();
  }

  public void connectedTo(Direction direction, ItemPipeBlockEntity pipe, BlockPos pos) {
    connections.add(direction);
  }

  public boolean isConnected(Direction direction) {
    return connections.contains(direction);
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
    super.saveAdditional(nbt, provider);
    // 接続方向（EnumSet）をビットマスクとして保存
    int connectionMask = 0;
    for (Direction direction : connections) connectionMask |= 1 << direction.ordinal();
    nbt.putInt("Connections", connectionMask);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
    super.loadAdditional(nbt, provider);

    // 接続方向を復元
    int connectionMask = nbt.getInt("Connections");
    connections.clear();
    for (Direction direction : Direction.values())
      if ((connectionMask & (1 << direction.ordinal())) != 0) connections.add(direction);
  }

  protected PipeItemBlock self() {
    return (PipeItemBlock) getBlockState().getBlock();
  }
}
