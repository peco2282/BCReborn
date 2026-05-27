/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.core.Direction;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;

public class AIRobotUseToolOnBlock extends AIRobot {

	private BlockIndex useToBlock;
	private int useCycles = 0;

	public AIRobotUseToolOnBlock(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotUseToolOnBlock(EntityRobotBase iRobot, BlockIndex index) {
		this(iRobot);

		useToBlock = index;
	}

	@Override
	public void start() {
		robot.aimItemAt(useToBlock.x, useToBlock.y, useToBlock.z);
		robot.setItemActive(true);
	}

	@Override
	public void update() {
		useCycles++;

		if (useCycles > 40) {
			ItemStack stack = robot.getMainHandItem();

			Player player = BuildCraftAPI.proxy.getBuildCraftPlayer((ServerLevel) robot.level())
					.get();
			BlockPos pos = useToBlock.toBlockPos();
			if (BlockUtils.harvestBlock((ServerLevel) robot.level(), pos, stack)) {
				if (stack.isDamageableItem()) {
					stack.hurtAndBreak(1, robot, r -> {});

					if (stack.getDamageValue() >= stack.getMaxDamage()) {
						robot.setItemInUse(ItemStack.EMPTY);
					}
				} else {
					robot.setItemInUse(ItemStack.EMPTY);
				}
			} else {
				setSuccess(false);
				if (!stack.isDamageableItem()) {
					BlockUtils.dropItem((ServerLevel) robot.level(), pos, 6000, stack);
					robot.setItemInUse(ItemStack.EMPTY);
				}
			}

			terminate();
		}
	}

	@Override
	public void end() {
		robot.setItemActive(false);
	}

	@Override
	public int getEnergyCost() {
		return 8;
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (useToBlock != null) {
			CompoundTag sub = new CompoundTag();
			useToBlock.writeTo(sub);
			nbt.put("blockFound", sub);
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);

		if (nbt.contains("blockFound")) {
			useToBlock = new BlockIndex(nbt.getCompound("blockFound"));
		}
	}
}
