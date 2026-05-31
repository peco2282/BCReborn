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
package com.peco2282.bcreborn.builders.blueprints;

import com.peco2282.bcreborn.api.library.LibraryTypeHandlerNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LibraryBookTypeHandler extends LibraryTypeHandlerNBT {
	public LibraryBookTypeHandler() {
		super("book");
	}

	@Override
	public boolean isHandler(ItemStack stack, HandlerType type) {
		if (type == HandlerType.STORE) {
			return stack.getItem() == Items.WRITTEN_BOOK;
		} else {
			return stack.getItem() == Items.WRITABLE_BOOK || stack.getItem() == Items.WRITTEN_BOOK;
		}
	}

	@Override
	public int getTextColor() {
		return 0x684804;
	}

	@Override
	public String getName(ItemStack stack) {
		String s = stack.getOrCreateTag().getString("title");
		return s.isEmpty() ? s : "";
	}

	@Override
	public ItemStack load(ItemStack stack, CompoundTag compound) {
		ItemStack out = new ItemStack(Items.WRITTEN_BOOK);
		CompoundTag outNBT = new CompoundTag();
		outNBT.putString("title", compound.getString("title"));
		outNBT.putString("author", compound.getString("author"));
		outNBT.put("pages", compound.getList("pages", Tag.TAG_STRING));
		out.save(outNBT);
		return out;
	}

	@Override
	public boolean store(ItemStack stack, CompoundTag compound) {
		CompoundTag inNBT = stack.getOrCreateTag();
		compound.putString("title", inNBT.getString("title"));
		compound.putString("author", inNBT.getString("author"));
		compound.put("pages", inNBT.getList("pages", Tag.TAG_STRING));
		return true;
	}
}
