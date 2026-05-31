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
package com.peco2282.bcreborn.energy.fluids;

import com.google.common.collect.ForwardingList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TankManager<T extends Tank> extends ForwardingList<T> implements IFluidHandler, List<T> {

	private List<T> tanks = new ArrayList<>();

	public TankManager() {
	}

	@SafeVarargs
  public TankManager(T... tanks) {
		addAll(Arrays.asList(tanks));
	}

	@Override
	protected List<T> delegate() {
		return tanks;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		for (Tank tank : tanks) {
			int used = tank.fill(resource, action);
			if (used > 0) {
				return used;
			}
		}
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource == null) {
			return FluidStack.EMPTY;
		}
		for (Tank tank : tanks) {
			if (!resource.isFluidEqual(tank.getFluid())) {
				continue;
			}
			FluidStack drained = tank.drain(resource.getAmount(), action);
			if (drained.getAmount() > 0) {
				return drained;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		for (Tank tank : tanks) {
			FluidStack drained = tank.drain(maxDrain, action);
			if (drained.getAmount() > 0) {
				return drained;
			}
		}
		return FluidStack.EMPTY;
	}
	/**
	 * Returns the number of fluid storage units ("tanks") available
	 *
	 * @return The number of tanks available
	 */
	@Override
	public int getTanks() {
		return size();
	}

	/**
	 * Returns the FluidStack in a given tank.
	 *
	 * <p>
	 * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
	 * altering internal contents. Any implementers who are able to detect modification via this method
	 * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
	 * </p>
	 *
	 * <p>
	 * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
	 * </p>
	 *
	 * @param tank Tank to query.
	 * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
	 */
	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return get(0).getFluidInTank(tank);
	}

	/**
	 * Retrieves the maximum fluid amount for a given tank.
	 *
	 * @param tank Tank to query.
	 * @return The maximum fluid amount held by the tank.
	 */
	@Override
	public int getTankCapacity(int tank) {
		int capacity = 0;
		for (var tanks : tanks) {
			capacity = tanks.getCapacity();
		}
		return capacity;
	}

	/**
	 * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
	 * basically always return TRUE for this.
	 *
	 * @param tank  Tank to query for validity
	 * @param stack Stack to test with for validity
	 * @return TRUE if the tank can hold the FluidStack, not considering current state.
	 * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
	 */
	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return true;
	}

	public void writeToNBT(CompoundTag data) {
		for (Tank tank : tanks) {
			tank.writeToNBT(data);
		}
	}

	public void readFromNBT(CompoundTag data) {
		for (Tank tank : tanks) {
			tank.readFromNBT(data);
		}
	}

	public void writeData(FriendlyByteBuf data) {
		for (Tank tank : tanks) {
			FluidStack fluidStack = tank.getFluid();
			if (fluidStack.getFluid() != null) {
				fluidStack.writeToPacket(data);
				data.writeInt(tank.colorRenderCache);
			} else {
				data.writeShort(-1);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void readData(FriendlyByteBuf data) {
		for (Tank tank : tanks) {
			int fluidId = data.readShort();
			FluidStack fs = FluidStack.readFromPacket(data);
			if (fs != null && fs != FluidStack.EMPTY) {
				tank.setFluid(fs);
				tank.colorRenderCache = data.readInt();
			} else {
				tank.setFluid(FluidStack.EMPTY);
				tank.colorRenderCache = 0xFFFFFF;
			}
		}
	}
}
