package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ListMatchHandlerOreDictionary extends ListMatchHandler {
	@Override
	public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
		Stream<TagKey<Item>> tags = stack.getTags();

		if (tags.findAny().isEmpty()) {
			if (type == Type.TYPE) {
				return StackHelper.isMatchingItem(stack, target, false, false);
			}
			return false;
		}

		// Re-fetch tags as stream is consumed
		List<TagKey<Item>> stackTags = stack.getTags().toList();
		List<TagKey<Item>> targetTags = target.getTags().toList();

		for (TagKey<Item> tag : stackTags) {
			if (targetTags.contains(tag)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isValidSource(Type type, ItemStack stack) {
		return stack.getTags().findAny().isPresent();
	}

	@Override
	public List<ItemStack> getClientExamples(Type type, ItemStack stack) {
		List<ItemStack> stacks = new ArrayList<>();
		stack.getTags().forEach(tag -> {
			BuiltInRegistries.ITEM.getTag(tag).ifPresent(t -> {
				t.forEach(holder -> stacks.add(new ItemStack(holder.value())));
			});
		});

		return stacks;
	}
}
