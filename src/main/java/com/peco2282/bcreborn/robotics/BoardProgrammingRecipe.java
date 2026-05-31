package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRegistry;
import com.peco2282.bcreborn.api.recipes.IProgrammingRecipe;
import com.peco2282.bcreborn.robotics.item.RedstoneBoardItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class BoardProgrammingRecipe implements IProgrammingRecipe {
	private record BoardSorter(BoardProgrammingRecipe recipe) implements Comparator<ItemStack> {

		@Override
			public int compare(ItemStack o1, ItemStack o2) {
				int i = (recipe.getEnergyCost(o1) - recipe.getEnergyCost(o2)) * 200;
				return i != 0 ? i : RedstoneBoardItem.getBoardNBT(o1).getID().compareTo(RedstoneBoardItem.getBoardNBT(o2).getID());
			}
		}

	@Override
	public String getId() {
		return "buildcraft:redstone_board";
	}

	@Override
	public List<ItemStack> getOptions(int width, int height) {
		List<ItemStack> options = new ArrayList<ItemStack>(width * height);
		for (RedstoneBoardNBT<?> nbt : RedstoneBoardRegistry.instance.getAllBoardNBTs()) {
			ItemStack stack = new ItemStack(RoboticsItems.REDSTONE_BOARD.get());
			nbt.createBoard(stack.getOrCreateTag());
			options.add(stack);
		}
		Collections.sort(options, new BoardSorter(this));
		return options;
	}

	@Override
	public int getEnergyCost(ItemStack option) {
		return RedstoneBoardRegistry.instance.getEnergyCost(
				RedstoneBoardRegistry.instance.getRedstoneBoard(option.getTag())
		);
	}

	@Override
	public boolean canCraft(ItemStack input) {
		return input.getItem() instanceof RedstoneBoardItem;
	}

	@Override
	public ItemStack craft(ItemStack input, ItemStack option) {
		return option.copy();
	}
}
