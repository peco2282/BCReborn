package com.peco2282.bcreborn.api.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ICropHandler {
    boolean isSeed(ItemStack stack);

    boolean canSustainPlant(Level world, ItemStack seed, BlockPos pos);

    boolean plantCrop(Level world, Player player, ItemStack seed, BlockPos pos);

    boolean isMature(BlockGetter blockAccess, BlockState state, BlockPos pos);

    boolean harvestCrop(Level world, BlockPos pos, List<ItemStack> drops);
}
