/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.item;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRegistry;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.common.utils.NBTUtils;
import com.peco2282.bcreborn.robotics.RoboticsItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotItem extends BuildCraftItem {

	public RobotItem() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		CompoundTag cpt = getNBT(stack);
		RedstoneBoardRobotNBT boardNBT = getRobotNBT(cpt);

		if (boardNBT != RedstoneBoardRegistry.instance.getEmptyRobotBoard()) {
			return 1;
		} else {
			return 16;
		}
	}

	public static RedstoneBoardRobotNBT getRobotNBT(ItemStack stack) {
		return getRobotNBT(getNBT(stack));
	}

	public static int getEnergy(ItemStack stack) {
		return getEnergy(getNBT(stack));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		CompoundTag cpt = getNBT(stack);
		RedstoneBoardRobotNBT boardNBT = getRobotNBT(cpt);

		if (boardNBT != RedstoneBoardRegistry.instance.getEmptyRobotBoard()) {
			boardNBT.addInformation(stack, level, tooltip, flag);

			int energy = getEnergy(cpt);
			// TODO: Add energy information when MAX_ENERGY is available
			tooltip.add(Component.literal("Energy: " + energy));
		}
	}

	public static ItemStack createRobotStack(RedstoneBoardRobotNBT board, int energy) {
		ItemStack robot = new ItemStack(RoboticsItems.ROBOT.get());
		CompoundTag boardCpt = new CompoundTag();
		board.createBoard(boardCpt);
		CompoundTag itemData = NBTUtils.getItemData(robot);
		itemData.put("board", boardCpt);
		itemData.putInt("energy", energy);
		return robot;
	}

	private static CompoundTag getNBT(ItemStack stack) {
		CompoundTag cpt = NBTUtils.getItemData(stack);
		if (!cpt.contains("board")) {
			RedstoneBoardRegistry.instance.getEmptyRobotBoard().createBoard(cpt);
		}
		return cpt;
	}

	private static RedstoneBoardRobotNBT getRobotNBT(CompoundTag cpt) {
		CompoundTag boardCpt = cpt.getCompound("board");
		return (RedstoneBoardRobotNBT) RedstoneBoardRegistry.instance.getRedstoneBoard(boardCpt);
	}

	private static int getEnergy(CompoundTag cpt) {
		return cpt.getInt("energy");
	}

	private static void setEnergy(CompoundTag cpt, int energy) {
		cpt.putInt("energy", energy);
	}
}
