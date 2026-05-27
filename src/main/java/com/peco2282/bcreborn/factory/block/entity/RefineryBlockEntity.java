package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.peco2282.bcreborn.factory.menu.RefineryMenu;
import org.jetbrains.annotations.Nullable;

public class RefineryBlockEntity extends BuildCraftBlockEntity implements IFluidHandler, IHasWork, net.minecraftforge.energy.IEnergyStorage, MenuProvider {

	public static int LIQUID_PER_SLOT = 4000;

	public FluidTank[] tanks = {new FluidTank(LIQUID_PER_SLOT), new FluidTank(LIQUID_PER_SLOT)};
	public FluidTank result = new FluidTank(LIQUID_PER_SLOT);

	public float animationSpeed = 1;
	private short animationStage = 0;
	private SafeTimeTracker updateNetworkTime = new SafeTimeTracker(20);
	private boolean isActive;

	public RefineryBlockEntity(BlockPos pos, BlockState state) {
		super(FactoryBlockEntityTypes.REFINERY.get(), pos, state);
		this.setBattery(new EnergyStorage(10000, 1500, 0));
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (level.isClientSide) {
			// Animation logic
			return;
		}

		if (updateNetworkTime.markTimeIfDelay(level)) {
			// Network update
		}

		isActive = false;
		// Crafting logic placeholder
	}

	@Override
	public void load(CompoundTag data) {
		super.load(data);
		tanks[0].readFromNBT(data.getCompound("tank1"));
		tanks[1].readFromNBT(data.getCompound("tank2"));
		result.readFromNBT(data.getCompound("result"));
		animationStage = data.getShort("animationStage");
		animationSpeed = data.getFloat("animationSpeed");
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		super.saveAdditional(data);
		CompoundTag t1 = new CompoundTag();
		tanks[0].writeToNBT(t1);
		data.put("tank1", t1);
		CompoundTag t2 = new CompoundTag();
		tanks[1].writeToNBT(t2);
		data.put("tank2", t2);
		CompoundTag tr = new CompoundTag();
		result.writeToNBT(tr);
		data.put("result", tr);
		data.putShort("animationStage", animationStage);
		data.putFloat("animationSpeed", animationSpeed);
	}

	@Override
	public boolean hasWork() {
		return isActive;
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("tile.refineryBlock.name");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
		return new RefineryMenu(id, playerInv, this);
	}

	@Override
	public boolean stillValid(Player player) {
		return super.stillValid(player);
	}

	@Override
	public int getTanks() {
		return 3;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (tank < 2) return tanks[tank].getFluid();
		return result.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return LIQUID_PER_SLOT;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return tank < 2;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		// Simplified fill logic
		int filled = tanks[0].fill(resource, action);
		if (filled == 0) filled = tanks[1].fill(resource, action);
		return filled;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return result.drain(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return result.drain(maxDrain, action);
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
}
