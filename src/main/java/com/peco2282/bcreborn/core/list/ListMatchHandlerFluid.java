package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import com.peco2282.bcreborn.common.utils.FluidUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.ArrayList;
import java.util.List;

public class ListMatchHandlerFluid extends ListMatchHandler {
	@Override
	public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
		if (type == Type.TYPE) {
			if (FluidUtils.isFluidContainer(stack) && FluidUtils.isFluidContainer(target)) {
				// We don't have a direct equivalent of drainFluidContainer easily without modifying the stack,
				// but we can check if they are the same type of container.
				return stack.getItem() == target.getItem();
			}
		} else if (type == Type.MATERIAL) {
			FluidStack fStack = FluidUtils.getFluidStackFromItemStack(stack);
			FluidStack fTarget = FluidUtils.getFluidStackFromItemStack(target);
			if (!fStack.isEmpty() && !fTarget.isEmpty()) {
				return fStack.isFluidEqual(fTarget);
			}
		}
		return false;
	}

	@Override
	public boolean isValidSource(Type type, ItemStack stack) {
		if (type == Type.TYPE) {
			return FluidUtils.isFluidContainer(stack);
		} else if (type == Type.MATERIAL) {
			return !FluidUtils.getFluidStackFromItemStack(stack).isEmpty();
		}
		return false;
	}

	@Override
	public List<ItemStack> getClientExamples(Type type, ItemStack stack) {
		// Example generation for fluids is complex in 1.20.1 due to the lack of a central registry of all filled containers.
		// We will return the stack itself for now.
		if (type == Type.MATERIAL || type == Type.TYPE) {
			if (isValidSource(type, stack)) {
				List<ItemStack> examples = new ArrayList<>();
				examples.add(stack);
				return examples;
			}
		}
		return null;
	}
}
