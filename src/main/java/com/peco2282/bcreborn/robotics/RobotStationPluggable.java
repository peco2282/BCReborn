package com.peco2282.bcreborn.robotics;

import java.util.List;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IDockingStationProvider;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RobotStationPluggable extends PipePluggable implements IDockingStationProvider {

	private DockingStationPipe station;

	public RobotStationPluggable() {
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
	}

	@Override
	public ItemStack[] getDropItems(IPipeTile pipe) {
		return new ItemStack[]{new ItemStack(RoboticsItems.ROBOT_STATION.get())};
	}

	@Override
	public DockingStation getStation() {
		return station;
	}

	@Override
	public boolean isBlocking(IPipeTile pipe, Direction direction) {
		return false;
	}

	@Override
	public void invalidate() {
		station = null;
	}

	@Override
	public void validate(IPipeTile pipe, Direction direction) {
		if (station == null) {
			station = new DockingStationPipe(pipe, direction);
		}
	}

	@Override
	public AABB getBoundingBox(Direction side) {
		return new AABB(0, 0, 0, 1, 1, 1);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IPipePluggableRenderer getRenderer() {
		return null;
	}

	@Override
	public void readData(FriendlyByteBuf stream) {
	}

	@Override
	public void writeData(FriendlyByteBuf stream) {
	}
}
