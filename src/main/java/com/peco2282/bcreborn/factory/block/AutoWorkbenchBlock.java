/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.factory.block;


import com.peco2282.bcreborn.api.transport.IItemPipe;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AutoWorkbenchBlock extends BuildCraftBlock {
	public AutoWorkbenchBlock() {
		super(Properties.of().sound(SoundType.WOOD).strength(3.0F));
	}

	@Override
	public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
		InteractionResult result = super.use(p_60503_, p_60504_, p_60505_, p_60506_, p_60507_, p_60508_);
		if (result.consumesAction()) {
			return result;
		}
		if (p_60506_.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}

		ItemStack held = p_60506_.getItemInHand(p_60507_);
		if (!held.isEmpty()) {
			Item heldItem = held.getItem();
			if (heldItem instanceof IItemPipe) {
				return InteractionResult.PASS;
			}
		}

		if (!p_60504_.isClientSide) {
			BlockEntity entity = p_60504_.getBlockEntity(p_60505_);
			if (entity instanceof MenuProvider provider)
				NetworkHooks.openScreen((ServerPlayer) p_60506_, provider);
		}

		return InteractionResult.sidedSuccess(p_60504_.isClientSide);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return null;
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {

	}

	@Override
	public boolean isRotatable() {
		return false;
	}
}
