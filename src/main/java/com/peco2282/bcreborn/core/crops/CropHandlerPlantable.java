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


import com.peco2282.bcreborn.api.crops.ICropHandler;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CropHandlerPlantable implements ICropHandler {

	private static final Set<Block> FORBIDDEN_BLOCKS = new HashSet<Block>();

	public static void forbidBlock(Block b) {
		FORBIDDEN_BLOCKS.add(b);
	}

	@Override
	public boolean isSeed(ItemStack stack) {
		if (stack.getItem() instanceof IPlantable) {
			return true;
		}

		if (stack.getItem() instanceof BlockItem item) {
			Block block = item.getBlock();
      return block instanceof IPlantable && !FORBIDDEN_BLOCKS.contains(block);
		}

		return false;
	}

	@Override
	public boolean canSustainPlant(Level world, ItemStack seed, BlockPos pos) {
		if (seed.getItem() instanceof IPlantable) {
			return world.getBlockState(pos).canSustainPlant(world, pos, Direction.UP,
					(IPlantable) seed.getItem())
					&& world.getBlockState(pos.above()).isAir();
		} else if (seed.getItem() instanceof BlockItem item && item.getBlock() instanceof IPlantable plantable) {
			BlockState state = world.getBlockState(pos);
			return state.canSustainPlant(world, pos, Direction.UP, plantable)
					&& state.getBlock() != item.getBlock()
					&& world.getBlockState(pos.above()).isAir();
		}
		return false;
	}

	@Override
	public boolean plantCrop(Level world, Player player, ItemStack seed, BlockPos pos) {
		return seed.useOn(new UseOnContext(player, player.getUsedItemHand(), new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false))).consumesAction();
	}

	@Override
	public boolean isMature(BlockGetter blockAccess, BlockState state, BlockPos pos) {
		Block block = state.getBlock();
		if (block == null || FORBIDDEN_BLOCKS.contains(block)) {
			return false;
		} else if (block instanceof TallGrassBlock
				|| block instanceof MelonBlock
				|| block instanceof MushroomBlock
				|| block instanceof DoublePlantBlock
				|| block == Blocks.PUMPKIN) {
			return true;
		} else if (block instanceof CropBlock crop) {
			return crop.isMaxAge(state);
		} else if (block instanceof NetherWartBlock) {
			return state.getValue(NetherWartBlock.AGE) == 3;
		} else if (block instanceof IPlantable) {
			if (pos.getY() > 0 && blockAccess.getBlockState(pos.below()).is(block)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean harvestCrop(Level world, BlockPos pos, List<ItemStack> drops) {
		if (!world.isClientSide) {
			if (BlockUtils.breakBlock((ServerLevel) world, pos, drops)) {
				return true;
			}
		}
		return false;
	}
}
