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
package com.peco2282.bcreborn.common.inventory;


import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.Iterator;

public final class InvUtils {

	/**
	 * Deactivate constructor
	 */
	private InvUtils() {
	}

	public static int countItems(Container inv, Direction side, IStackFilter filter) {
		int count = 0;
		for (IInvSlot slot : InventoryIterator.getIterable(inv, side)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && filter.matches(stack)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	public static boolean containsItem(Container inv, Direction side, IStackFilter filter) {
		for (IInvSlot slot : InventoryIterator.getIterable(inv, side)) {
			ItemStack stack = slot.getStackInSlot();
			if (stack != null && filter.matches(stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if there is room for the ItemStack in the inventory.
	 *
	 * @param stack The ItemStack
	 * @param dest The Container
	 * @return true if room for stack
	 */
	public static boolean isRoomForStack(ItemStack stack, Direction side, Container dest) {
		if (stack == null || dest == null) {
			return false;
		}
		ITransactor tran = Transactor.getTransactorFor(dest);
		return tran.add(stack, side, false).getCount() > 0;
	}

	/**
	 * Attempts to move a single item from one inventory to another.
	 *
	 * @param source
	 * @param dest
	 * @param filter
	 *            an IStackFilter to match against
	 * @return null if nothing was moved, the stack moved otherwise
	 */
	public static ItemStack moveOneItem(Container source, Direction output, Container dest, Direction intput, IStackFilter filter) {
		ITransactor imSource = Transactor.getTransactorFor(source);
		ItemStack stack = imSource.remove(filter, output, false);
		if (stack != null) {
			ITransactor imDest = Transactor.getTransactorFor(dest);
			int moved = imDest.add(stack, intput, true).getCount();
			if (moved > 0) {
				imSource.remove(filter, output, true);
				return stack;
			}
		}
		return null;
	}

	/* STACK DROPS */
	public static void dropItems(Level world, ItemStack stack, int i, int j, int k) {
		if (stack == null || stack.isEmpty()) {
			return;
		}

		float f1 = 0.7F;
		double d = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d1 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		double d2 = (world.random.nextFloat() * f1) + (1.0F - f1) * 0.5D;
		ItemEntity entityitem = new ItemEntity(world, i + d, j + d1, k + d2, stack);
		entityitem.setPickUpDelay(10);

		world.addFreshEntity(entityitem);
	}

	public static void dropItems(Level world, Container inv, int i, int j, int k) {
		for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
			ItemStack items = inv.getItem(slot);

			if (items != null && items.getCount() > 0) {
				dropItems(world, items.copy(), i, j, k);
			}
		}
	}

	public static void wipeInventory(Container inv) {
		for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
			inv.setItem(slot, ItemStack.EMPTY);
		}
	}

	public static CompoundTag getItemData(ItemStack stack) {
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt == null) {
			nbt = new CompoundTag();
			stack.save(nbt);
		}
		return nbt;
	}

	public static void addItemToolTip(ItemStack stack, String msg) {
		CompoundTag nbt = getItemData(stack);
		CompoundTag display = nbt.getCompound("display");
		nbt.put("display", display);
		ListTag lore = display.getList("Lore", ListTag.TAG_STRING);
		display.put("Lore", lore);
		lore.add(StringTag.valueOf(msg));
	}

	public static void writeInvToNBT(Container inv, String tag, CompoundTag data) {
		ListTag list = new ListTag();
		for (byte slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack stack = inv.getItem(slot);
			if (!stack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putByte("Slot", slot);
				stack.save(itemTag);
				list.add(itemTag);
			}
		}
		data.put(tag, list);
	}

	public static void readInvFromNBT(Container inv, String tag, CompoundTag data) {
		ListTag list = data.getList(tag, ListTag.TAG_COMPOUND);
		for (byte entry = 0; entry < list.size(); entry++) {
			CompoundTag itemTag = list.getCompound(entry);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getContainerSize()) {
				ItemStack stack = ItemStack.of(itemTag);
				inv.setItem(slot, stack);
			}
		}
	}

	public static void readStacksFromNBT(CompoundTag nbt, String name, ItemStack[] stacks) {
		ListTag nbttaglist = nbt.getList(name, ListTag.TAG_COMPOUND);

		for (int i = 0; i < stacks.length; ++i) {
			if (i < nbttaglist.size()) {
				CompoundTag nbttagcompound2 = nbttaglist.getCompound(i);

				stacks[i] = ItemStack.of(nbttagcompound2);
			} else {
				stacks[i] = null;
			}
		}
	}

	public static void writeStacksToNBT(CompoundTag nbt, String name, ItemStack[] stacks) {
		ListTag nbttaglist = new ListTag();

		for (ItemStack stack : stacks) {
			CompoundTag cpt = new CompoundTag();
			nbttaglist.add(cpt);
			if (stack != null) {
				stack.save(cpt);
			}

		}

		nbt.put(name, nbttaglist);
	}

	public static ItemStack consumeItem(ItemStack stack) {
		if (stack.getCount() == 1) {
			if (stack.getItem().hasCraftingRemainingItem()) {
				return new ItemStack(stack.getItem().getCraftingRemainingItem());
			} else {
				return ItemStack.EMPTY;
			}
		} else {
			stack.shrink(1);

			return stack;
		}
	}

	/**
	 * Ensures that the given inventory is the full inventory, i.e. takes double
	 * chests into account.
	 *
	 * @param inv
	 * @return Modified inventory if double chest, unmodified otherwise.
	 */
	public static Container getInventory(Container inv) {
		if (inv instanceof ChestBlockEntity) {
			ChestBlockEntity adjacent = BlockUtils.getOtherDoubleChest((ChestBlockEntity) inv);
			if (adjacent != null) {
				return new CompoundContainer(inv, adjacent);
			}
			return inv;
		}
		return inv;
	}

	public static IInvSlot getItem(Container inv, IStackFilter filter) {
		for (IInvSlot s : InventoryIterator.getIterable(inv)) {
			if (!s.getStackInSlot().isEmpty() && filter.matches(s.getStackInSlot())) {
				return s;
			}
		}

		return null;
	}

	public static Iterable<IInvSlot> getItems(final Container inv, final IStackFilter filter) {
		return new Iterable<IInvSlot>() {
			@Override
			public Iterator<IInvSlot> iterator() {
				return new Iterator<IInvSlot>() {
					private final Iterator<IInvSlot> parent = InventoryIterator.getIterable(inv).iterator();
					private boolean searched = false;
					private IInvSlot next;

					private void find() {
						next = null;
						searched = true;

						while (parent.hasNext()) {
							IInvSlot s = parent.next();
							if (!s.getStackInSlot().isEmpty() && filter.matches(s.getStackInSlot())) {
								next = s;
								return;
							}
						}
					}

					@Override
					public boolean hasNext() {
						if (!searched) {
							find();
						}

						return next != null;
					}

					@Override
					public IInvSlot next() {
						if (!searched) {
							find();
						}

						IInvSlot current = next;
						find();
						return current;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException("Remove not supported.");
					}
				};
			}
		};
	}
}
