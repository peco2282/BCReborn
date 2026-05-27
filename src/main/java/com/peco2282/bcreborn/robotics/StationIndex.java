package com.peco2282.bcreborn.robotics;


import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.DockingStation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class StationIndex {

	public BlockIndex index = new BlockIndex();
	public Direction side = Direction.UP;

	protected StationIndex() {
	}

	public StationIndex(Direction iSide, int x, int y, int z) {
		side = iSide;
		index = new BlockIndex(x, y, z);
	}

	public StationIndex(DockingStation station) {
		side = station.side();
		index = station.index();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		StationIndex compareId = (StationIndex) obj;

		return index.equals(compareId.index)
				&& side == compareId.side;
	}

	@Override
	public int hashCode() {
		return (index.hashCode() * 37) + side.get3DDataValue();
	}

	public void writeToNBT(CompoundTag nbt) {
		CompoundTag indexNBT = new CompoundTag();
		index.writeTo(indexNBT);
		nbt.put("index", indexNBT);
		nbt.putByte("side", (byte) side.get3DDataValue());
	}

	protected void readFromNBT(CompoundTag nbt) {
		index = new BlockIndex(nbt.getCompound("index"));
		side = Direction.from3DDataValue(nbt.getByte("side"));
	}
}
