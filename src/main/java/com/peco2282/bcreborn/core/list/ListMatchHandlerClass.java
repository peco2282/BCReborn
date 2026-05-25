package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.api.lists.ListRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ListMatchHandlerClass extends ListMatchHandler {
	@Override
	public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
		if (type == Type.TYPE) {
			Class<? extends Item> kl = stack.getItem().getClass();
			return ListRegistry.itemClassAsType.contains(kl) && kl.equals(target.getItem().getClass());
		}
		return false;
	}

	@Override
	public boolean isValidSource(Type type, ItemStack stack) {
		if (type == Type.TYPE) {
			Class<? extends Item> kl = stack.getItem().getClass();
			return ListRegistry.itemClassAsType.contains(kl);
		}
		return false;
	}
}
