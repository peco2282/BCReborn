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

import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.utils.LaserUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Box implements IBox {
    public enum Kind {
        LASER_RED,
        LASER_YELLOW,
        LASER_GREEN,
        LASER_BLUE,
        STRIPES,
        BLUE_STRIPES,
    }

    public Kind kind = Kind.LASER_RED;
    public int xMin, yMin, zMin, xMax, yMax, zMax;
    public boolean initialized;
    public LaserData[] lasersData;

    public Box() {
        reset();
    }

    public Box(BlockEntity e) {
        BlockPos pos = e.getBlockPos();
        initialize(pos.getX(), pos.getY(), pos.getZ(),
            pos.getX() + 1, pos.getY() + 1,
            pos.getZ() + 1);
    }

    public Box(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        this();
        initialize(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public void reset() {
        initialized = false;
        xMin = Integer.MAX_VALUE;
        yMin = Integer.MAX_VALUE;
        zMin = Integer.MAX_VALUE;
        xMax = Integer.MAX_VALUE;
        yMax = Integer.MAX_VALUE;
        zMax = Integer.MAX_VALUE;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        if (xMin < xMax) {
            this.xMin = xMin;
            this.xMax = xMax;
        } else {
            this.xMin = xMax;
            this.xMax = xMin;
        }

        if (yMin < yMax) {
            this.yMin = yMin;
            this.yMax = yMax;
        } else {
            this.yMin = yMax;
            this.yMax = yMin;
        }

        if (zMin < zMax) {
            this.zMin = zMin;
            this.zMax = zMax;
        } else {
            this.zMin = zMax;
            this.zMax = zMin;
        }

        initialized = !(this.xMin == Integer.MAX_VALUE || this.yMin == Integer.MAX_VALUE || this.zMin == Integer.MAX_VALUE
                || this.xMax == Integer.MAX_VALUE || this.yMax == Integer.MAX_VALUE || this.zMax == Integer.MAX_VALUE);
    }

   	public void initialize(IAreaProvider box) {
		initialize(box.xMin(), box.yMin(), box.zMin(), box.xMax(), box.yMax(), box.zMax());
	}

	public void initialize(Box box) {
		initialize(box.xMin, box.yMin, box.zMin, box.xMax, box.yMax, box.zMax);
	}

    public void initialize(CompoundTag nbt) {
        initialize(
                nbt.getInt("xMin"), nbt.getInt("yMin"), nbt.getInt("zMin"),
                nbt.getInt("xMax"), nbt.getInt("yMax"), nbt.getInt("zMax")
        );
    }

    public List<BlockIndex> getBlocksInArea() {
        List<BlockIndex> blocks = new ArrayList<>();
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    blocks.add(new BlockIndex(x, y, z));
                }
            }
        }
        return blocks;
    }

    @Override
    public Box expand(int amount) {
        xMin -= amount;
        yMin -= amount;
        zMin -= amount;
        xMax += amount;
        yMax += amount;
        zMax += amount;
        return this;
    }

    @Override
    public IBox contract(int amount) {
        return expand(-amount);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        return contains((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public boolean contains(int x, int y, int z) {
        return x >= xMin && x <= xMax && y >= yMin && y <= yMax && z >= zMin && z <= zMax;
    }

    public boolean contains(Position p) {
        return contains((int) p.x, (int) p.y, (int) p.z);
    }

    public boolean contains(BlockIndex i) {
        return contains(i.x, i.y, i.z);
    }

    @Override
    public Position pMin() {
        return new Position(xMin, yMin, zMin);
    }

    @Override
    public Position pMax() {
        return new Position(xMax, yMax, zMax);
    }

    public int sizeX() {
        return xMax - xMin + 1;
    }

    public int sizeY() {
        return yMax - yMin + 1;
    }

    public int sizeZ() {
        return zMax - zMin + 1;
    }

    public double centerX() {
        return xMin + sizeX() / 2.0;
    }

    public double centerY() {
        return yMin + sizeY() / 2.0;
    }

    public double centerZ() {
        return zMin + sizeZ() / 2.0;
    }

    public Box rotateLeft() {
        Box nBox = new Box();
        nBox.xMin = (sizeZ() - 1) - zMin;
        nBox.yMin = yMin;
        nBox.zMin = xMin;
        nBox.xMax = (sizeZ() - 1) - zMax;
        nBox.yMax = yMax;
        nBox.zMax = xMax;
        nBox.reorder();
        return nBox;
    }

    public void reorder() {
        int tmp;
        if (xMin > xMax) { tmp = xMin; xMin = xMax; xMax = tmp; }
        if (yMin > yMax) { tmp = yMin; yMin = yMax; yMax = tmp; }
        if (zMin > zMax) { tmp = zMin; zMin = zMax; zMax = tmp; }
    }

    @Override
    public void createLaserData() {
        lasersData = LaserUtils.createLaserDataBox(xMin, yMin, zMin, xMax, yMax, zMax, LaserKind.Blue).toArray(new LaserData[0]);
    }

    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("xMin", xMin);
        nbt.putInt("yMin", yMin);
        nbt.putInt("zMin", zMin);
        nbt.putInt("xMax", xMax);
        nbt.putInt("yMax", yMax);
        nbt.putInt("zMax", zMax);
    }

    public Box extendToEncompass(Box toBeContained) {
        if (toBeContained == null || !toBeContained.initialized) {
            return this;
        }
        if (toBeContained.xMin < xMin) xMin = toBeContained.xMin;
        if (toBeContained.yMin < yMin) yMin = toBeContained.yMin;
        if (toBeContained.zMin < zMin) zMin = toBeContained.zMin;
        if (toBeContained.xMax > xMax) xMax = toBeContained.xMax;
        if (toBeContained.yMax > yMax) yMax = toBeContained.yMax;
        if (toBeContained.zMax > zMax) zMax = toBeContained.zMax;
        return this;
    }

    public Box extendToEncompass(Position toBeContained) {
        if (toBeContained.x < xMin) xMin = (int) toBeContained.x - 1;
        if (toBeContained.y < yMin) yMin = (int) toBeContained.y - 1;
        if (toBeContained.z < zMin) zMin = (int) toBeContained.z - 1;
        if (toBeContained.x > xMax) xMax = (int) toBeContained.x + 1;
        if (toBeContained.y > yMax) yMax = (int) toBeContained.y + 1;
        if (toBeContained.z > zMax) zMax = (int) toBeContained.z + 1;
        return this;
    }

    public Box extendToEncompass(BlockIndex toBeContained) {
        if (toBeContained.x < xMin) xMin = toBeContained.x - 1;
        if (toBeContained.y < yMin) yMin = toBeContained.y - 1;
        if (toBeContained.z < zMin) zMin = toBeContained.z - 1;
        if (toBeContained.x > xMax) xMax = toBeContained.x + 1;
        if (toBeContained.y > yMax) yMax = toBeContained.y + 1;
        if (toBeContained.z > zMax) zMax = toBeContained.z + 1;
        return this;
    }


    public AABB getBoundingBox() {
        return AABB.of(
            new BoundingBox(
                xMin, yMin, zMin,
            xMax, yMax, zMax
            )
        );
    }


    @Override
    public double distanceTo(BlockIndex index) {
        return Math.sqrt(distanceToSquared(index));
    }

    @Override
    public double distanceToSquared(BlockIndex index) {
        int dx = index.x - (xMin + (xMax - xMin + 1));
        int dy = index.y - (yMin + (yMax - yMin + 1));
        int dz = index.z - (zMin + (zMax - zMin + 1));
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public BlockIndex getRandomBlockIndex(Random rand) {
        int x = (xMax > xMin) ? xMin + rand.nextInt(xMax - xMin + 1) : xMin;
        int y = (yMax > yMin) ? yMin + rand.nextInt(yMax - yMin + 1) : yMin;
        int z = (zMax > zMin) ? zMin + rand.nextInt(zMax - zMin + 1) : zMin;
        return new BlockIndex(x, y, z);
    }

    public void readData(FriendlyByteBuf stream) {
        byte flags = stream.readByte();
        xMin = stream.readInt();
        yMin = stream.readShort();
        zMin = stream.readInt();
        xMax = stream.readInt();
        yMax = stream.readShort();
        zMax = stream.readInt();
        initialized = (flags & 64) != 0;
    }

    public void writeData(FriendlyByteBuf stream) {
        stream.writeByte(initialized ? 64 : 0);
        stream.writeInt(xMin);
        stream.writeShort(yMin);
        stream.writeInt(zMin);
        stream.writeInt(xMax);
        stream.writeShort(yMax);
        stream.writeInt(zMax);
    }

    @Override
    public String toString() {
        return "{" + xMin + ", " + xMax + "}, {" + yMin + ", " + yMax + "}, {" + zMin + ", " + zMax + "}";
    }
}
