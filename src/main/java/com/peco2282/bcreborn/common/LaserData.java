/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common;


import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class LaserData implements ISerializable {
	public Position head = new Position(0, 0, 0);
	public Position tail = new Position(0, 0, 0);
	public boolean isVisible = true;
	public boolean isGlowing = false;

	public double renderSize = 1.0 / 16.0;
	public double angleY = 0;
	public double angleZ = 0;

	public double wavePosition = 0;
	public int laserTexAnimation = 0;

	// Size of the wave, from 0 to 1
	public float waveSize = 1F;

	public LaserData() {

	}

	public LaserData(Position tail, Position head) {
		this.tail.x = tail.x;
		this.tail.y = tail.y;
		this.tail.z = tail.z;

		this.head.x = head.x;
		this.head.y = head.y;
		this.head.z = head.z;
	}

	public void update() {
		double dx = head.x - tail.x;
		double dy = head.y - tail.y;
		double dz = head.z - tail.z;

		renderSize = Math.sqrt(dx * dx + dy * dy + dz * dz);
		angleZ = 360 - (Math.atan2(dz, dx) * 180.0 / Math.PI + 180.0);
		dx = Math.sqrt(renderSize * renderSize - dy * dy);
		angleY = -Math.atan2(dy, dx) * 180.0 / Math.PI;
	}

	public void iterateTexture() {
		laserTexAnimation = (laserTexAnimation + 1) % 40;
	}

	public void writeToNBT(CompoundTag nbt) {
		CompoundTag headNbt = new CompoundTag();
		head.writeToNBT(headNbt);
		nbt.put("head", headNbt);

		CompoundTag tailNbt = new CompoundTag();
		tail.writeToNBT(tailNbt);
		nbt.put("tail", tailNbt);

		nbt.putBoolean("isVisible", isVisible);
	}

	public void readFromNBT(CompoundTag nbt) {
		head.readFromNBT(nbt.getCompound("head"));
		tail.readFromNBT(nbt.getCompound("tail"));
		isVisible = nbt.getBoolean("isVisible");
	}

	@Override
	public void readData(FriendlyByteBuf stream) {
		head.readData(stream);
		tail.readData(stream);
		int flags = stream.readUnsignedByte();
		isVisible = (flags & 1) != 0;
		isGlowing = (flags & 2) != 0;
	}

	@Override
	public void writeData(FriendlyByteBuf stream) {
		head.writeData(stream);
		tail.writeData(stream);
		int flags = (isVisible ? 1 : 0) | (isGlowing ? 2 : 0);
		stream.writeByte(flags);
	}
}
