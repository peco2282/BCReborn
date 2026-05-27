package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.peco2282.bcreborn.factory.menu.HopperMenu;
import org.jetbrains.annotations.Nullable;

public class HopperBlockEntity extends BuildCraftBlockEntity implements Container, IRedstoneEngineReceiver, net.minecraftforge.energy.IEnergyStorage, MenuProvider {

	private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
	private boolean isEmpty = true;

	public HopperBlockEntity(BlockPos pos, BlockState state) {
		super(FactoryBlockEntityTypes.HOPPER.get(), pos, state);
		this.setBattery(new EnergyStorage(10, 10, 0));
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return getBattery().receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return getBattery().extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return getBattery().getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return getBattery().getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return getBattery().canExtract();
	}

	@Override
	public boolean canReceive() {
		return getBattery().canReceive();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ContainerHelper.loadAllItems(nbt, this.inventory);
		updateIsEmpty();
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, this.inventory);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide || isEmpty || level.getGameTime() % 2 != 0) {
			return;
		}

		BlockEntity outputTile = level.getBlockEntity(pos.below());
		if (outputTile == null) return;

		// TODO: Implement item injection logic using capabilities or IInjectable
		// For now, simple implementation if it's a Container
		if (outputTile instanceof Container container) {
			for (int i = 0; i < getContainerSize(); i++) {
				ItemStack stack = getItem(i);
				if (!stack.isEmpty()) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					ItemStack remaining = insertItem(container, copy);
					if (remaining.isEmpty()) {
						removeItem(i, 1);
						return;
					}
				}
			}
		}
	}

	private ItemStack insertItem(Container container, ItemStack stack) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			if (container.canPlaceItem(i, stack)) {
				ItemStack target = container.getItem(i);
				if (target.isEmpty()) {
					container.setItem(i, stack);
					return ItemStack.EMPTY;
				} else if (ItemStack.isSameItemSameTags(target, stack)) {
					int count = Math.min(stack.getCount(), target.getMaxStackSize() - target.getCount());
					target.grow(count);
					stack.shrink(count);
					if (stack.isEmpty()) return ItemStack.EMPTY;
				}
			}
		}
		return stack;
	}

	private void updateIsEmpty() {
		isEmpty = true;
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				isEmpty = false;
				break;
			}
		}
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		return isEmpty;
	}

	@Override
	public ItemStack getItem(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = ContainerHelper.removeItem(inventory, slot, count);
		updateIsEmpty();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = ContainerHelper.takeItem(inventory, slot);
		updateIsEmpty();
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		updateIsEmpty();
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("tile.hopperBlock.name");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
		return new HopperMenu(id, playerInv, this);
	}

	@Override
	public boolean stillValid(Player player) {
		return super.stillValid(player);
	}

	@Override
	public void clearContent() {
		inventory.clear();
		isEmpty = true;
	}

	@Override
	public boolean canConnectRedstoneEngine(Direction side) {
		return side != Direction.UP && side != Direction.DOWN;
	}
}
