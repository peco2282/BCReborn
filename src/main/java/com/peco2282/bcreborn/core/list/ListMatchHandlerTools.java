package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

public class ListMatchHandlerTools extends ListMatchHandler {
	@Override
	public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
		if (type == Type.TYPE) {
			if (stack.getItem() instanceof TieredItem source && target.getItem() instanceof TieredItem tieredTarget) {
				if (precise) {
					return source.getTier() == tieredTarget.getTier() && source.getClass() == tieredTarget.getClass();
				} else {
					return source.getClass() == tieredTarget.getClass();
				}
			}
		}
		return false;
	}

	@Override
	public boolean isValidSource(Type type, ItemStack stack) {
		return stack.getItem() instanceof TieredItem;
	}
}
