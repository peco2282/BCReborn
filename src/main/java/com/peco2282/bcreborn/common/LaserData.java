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
package com.peco2282.bcreborn.common;


import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class LaserData implements ISerializable {
	public LaserKind kind = LaserKind.Red;
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
		this(tail, head, LaserKind.Red);
	}

	public LaserData(Position tail, Position head, LaserKind kind) {
		this.tail.x = tail.x;
		this.tail.y = tail.y;
		this.tail.z = tail.z;

		this.head.x = head.x;
		this.head.y = head.y;
		this.head.z = head.z;

		this.kind = kind;
	}

	public LaserData(net.minecraft.world.phys.Vec3 tail, net.minecraft.world.phys.Vec3 head, LaserKind kind) {
		this(new Position(tail.x, tail.y, tail.z), new Position(head.x, head.y, head.z), kind);
	}

	public LaserData(CompoundTag nbt) {
		readFromNBT(nbt);
	}

	public void update() {
		double dx = head.x - tail.x;
		double dy = head.y - tail.y;
		double dz = head.z - tail.z;

		renderSize = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (renderSize > 0) {
			// angleZ: Y軸周りの回転（XZ平面上での方位）
			// atan2(dz, dx) はX軸正方向からZ軸正方向への角度を返す
			// LaserRendererではX軸方向に伸びるモデルを回転させる
			angleZ = -Math.toDegrees(Math.atan2(dz, dx));
			
			// angleY: Z軸（または傾いたX軸）周りの回転（仰角）
			// dxz はXZ平面上での距離
			double dxz = Math.sqrt(dx * dx + dz * dz);
			angleY = Math.toDegrees(Math.atan2(dy, dxz));
		}
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
		nbt.putBoolean("isGlowing", isGlowing);
		nbt.putInt("kind", kind.ordinal());
	}

	public void readFromNBT(CompoundTag nbt) {
		head.readFromNBT(nbt.getCompound("head"));
		tail.readFromNBT(nbt.getCompound("tail"));
		isVisible = nbt.getBoolean("isVisible");
		isGlowing = nbt.getBoolean("isGlowing");
		if (nbt.contains("kind")) {
			kind = LaserKind.values()[nbt.getInt("kind") % LaserKind.values().length];
		}
	}

	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readData(FriendlyByteBuf stream) {
		head.readData(stream);
		tail.readData(stream);
		int flags = stream.readUnsignedByte();
		isVisible = (flags & 1) != 0;
		isGlowing = (flags & 2) != 0;
		kind = LaserKind.values()[stream.readUnsignedByte() % LaserKind.values().length];
	}

	@Override
	public void writeData(FriendlyByteBuf stream) {
		head.writeData(stream);
		tail.writeData(stream);
		int flags = (isVisible ? 1 : 0) | (isGlowing ? 2 : 0);
		stream.writeByte(flags);
		stream.writeByte(kind.ordinal());
	}

	public BlockPos getCenter() {
		BlockPos head = this.head.toBlockPos();
		BlockPos tail = this.tail.toBlockPos();
		return new BlockPos((head.getX() + tail.getX()) / 2, (head.getY() + tail.getY()) / 2, (head.getZ() + tail.getZ()) / 2);
	}

	@Override
	public String toString() {
		return "LaserData{" + "head=" + head.toBlockPos() + ", tail=" + tail.toBlockPos() + ", isVisible=" + isVisible + ", isGlowing=" + isGlowing + ", kind=" + kind + '}';
	}
}
