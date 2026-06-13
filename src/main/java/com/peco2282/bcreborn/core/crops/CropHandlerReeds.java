/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.core.crops;
import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.crops.ICropHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

import java.util.List;

public class CropHandlerReeds implements ICropHandler {
  public static final CropHandlerReeds INSTANCE = new CropHandlerReeds();
  private CropHandlerReeds() {}

	@Override
	public boolean isSeed(ItemStack stack) {
		return stack.is(Items.SUGAR_CANE);
	}

	@Override
	public boolean canSustainPlant(Level world, ItemStack seed, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.canSustainPlant(world, pos, Direction.UP, (IPlantable) Blocks.SUGAR_CANE)
				&& !state.is(Blocks.SUGAR_CANE)
				&& world.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean plantCrop(Level world, Player player, ItemStack seed, BlockPos pos) {
		return CropManager.plantCrop(world, player, seed, pos);
	}

	@Override
	public boolean isMature(BlockGetter blockAccess, BlockState state, BlockPos pos) {
		return false;
	}

	@Override
	public boolean harvestCrop(Level world, BlockPos pos, List<ItemStack> drops) {
		return false;
	}
}
