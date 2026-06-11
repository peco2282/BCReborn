package com.peco2282.bcreborn.core.recipes;

import com.peco2282.bcreborn.api.recipes.IFlexibleCrafter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Use this class for simulated crafts.
 */
public class FakeFlexibleCrafter implements IFlexibleCrafter {
	private final IFlexibleCrafter original;
	private int[] usedItems, usedFluids;

	public FakeFlexibleCrafter(IFlexibleCrafter original) {
		this.original = original;
		this.usedFluids = new int[original.getCraftingFluidStackSize()];
		this.usedItems = new int[original.getCraftingItemStackSize()];
	}

	@Override
	public ItemStack getCraftingItemStack(int slotId) {
		ItemStack output = original.getCraftingItemStack(slotId);
		if (usedItems[slotId] == 0) {
			return output;
		} else if (output.getCount() <= usedItems[slotId]) {
			return ItemStack.EMPTY;
		}
		output = output.copy();
		output.setCount(output.getCount() - usedItems[slotId]);
		return output;
	}

	@Override
	public ItemStack decrCraftingItemStack(int slotId, int amount) {
		ItemStack output = original.getCraftingItemStack(slotId);
		int result = Math.min(output.getCount() - usedItems[slotId], amount);
		usedItems[slotId] += result;

		if (result == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack decrOut = output.copy();
		decrOut.setCount(result);
		return decrOut;
	}

	@Override
	public int getCraftingItemStackSize() {
		return this.usedItems.length;
	}

	@Override
	public FluidStack getCraftingFluidStack(int slotId) {
		FluidStack output = original.getCraftingFluidStack(slotId);
		if (usedFluids[slotId] == 0) {
			return output;
		} else if (output.getAmount() <= usedFluids[slotId]) {
			return FluidStack.EMPTY;
		}
		output = output.copy();
		output.setAmount(output.getAmount() - usedFluids[slotId]);
		return output;
	}

	@Override
	public FluidStack decrCraftingFluidStack(int slotId, int amount) {
		FluidStack output = original.getCraftingFluidStack(slotId);
		int result = Math.min(output.getAmount() - usedFluids[slotId], amount);
		usedFluids[slotId] += result;

		if (result == 0) {
			return FluidStack.EMPTY;
		}
		FluidStack decrOut = output.copy();
		decrOut.setAmount(result);
		return decrOut;
	}

	@Override
	public int getCraftingFluidStackSize() {
		return this.usedFluids.length;
	}
}
