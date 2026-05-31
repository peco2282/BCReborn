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
package com.peco2282.bcreborn.robotics;

import java.util.ArrayList;
import java.util.List;

import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRegistry;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import com.peco2282.bcreborn.robotics.item.RobotItem;
import net.minecraft.world.item.ItemStack;


public class RobotIntegrationRecipe {

	public List<ItemStack> generateExampleInput() {
		ArrayList<ItemStack> example = new ArrayList<ItemStack>();
		example.add(RobotItem.createRobotStack(RedstoneBoardRegistry.instance.getEmptyRobotBoard(), 0));
		return example;
	}

	public List<List<ItemStack>> generateExampleExpansions() {
		ArrayList<List<ItemStack>> list = new ArrayList<List<ItemStack>>();
		ArrayList<ItemStack> example = new ArrayList<ItemStack>();
		for (RedstoneBoardNBT<?> nbt : RedstoneBoardRegistry.instance.getAllBoardNBTs()) {
			ItemStack stack = new ItemStack(RoboticsItems.REDSTONE_BOARD.get());
			nbt.createBoard(stack.getOrCreateTag());
			example.add(stack);
		}
		list.add(example);
		return list;
	}

	public List<ItemStack> generateExampleOutput() {
		ArrayList<ItemStack> example = new ArrayList<ItemStack>();
		for (RedstoneBoardNBT<?> nbt : RedstoneBoardRegistry.instance.getAllBoardNBTs()) {
			example.add(RobotItem.createRobotStack((RedstoneBoardRobotNBT) nbt, 0));
		}
		return example;
	}

	public boolean isValidInput(ItemStack input) {
		return input.getItem() instanceof RobotItem;
	}

	public boolean isValidExpansion(ItemStack input, ItemStack expansion) {
		return expansion.getItem() instanceof RedstoneBoardItem;
	}

	public ItemStack craft(ItemStack input, List<ItemStack> expansions, boolean preview) {
		if (!preview) {
			expansions.get(0).shrink(1);
		}
		RedstoneBoardRobotNBT boardNBT = (RedstoneBoardRobotNBT) RedstoneBoardItem.getBoardNBT(expansions.get(0));

		int energy = RobotItem.getEnergy(input);
		if (energy == 0) {
			energy = EntityRobotBase.SAFETY_ENERGY;
		}
		return RobotItem.createRobotStack(boardNBT, energy);
	}
}
