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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.item.IToolWrench;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;

public class IronItemPipeBlockEntity extends ItemPipeBlockEntity implements IToolWrench {
  private Direction transportDirection = Direction.NORTH; // 搬送方向の初期値

  public IronItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.IRON_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.IRON);
  }

  @Contract(pure = true)
  public static void tick(
      Level world, BlockPos pos, BlockState state, @NotNull IronItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  protected void update(Level level, BlockPos pos, BlockState state) {}

  @Override
  public boolean canUseWrench(
      Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {
    return true;
  }

  @Override
  public void useWrench(
      Player player, InteractionHand hand, ItemStack wrench, BlockHitResult hit) {}

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
    super.saveAdditional(nbt, provider);

    // 搬送方向を保存
    nbt.putInt("TransportDirection", transportDirection.ordinal());
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
    super.loadAdditional(nbt, provider);

    // 搬送方向を復元
    transportDirection = Direction.values()[nbt.getInt("TransportDirection")];
  }
}
