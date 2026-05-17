/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.api.robots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class ResourceIdBlock extends ResourceId {
    public BlockPos index = BlockPos.ZERO;
    public Direction side = null;

    public ResourceIdBlock() {
    }

    public ResourceIdBlock(int x, int y, int z) {
        index = new BlockPos(x, y, z);
    }

    public ResourceIdBlock(BlockPos iIndex) {
        index = iIndex;
    }

    public ResourceIdBlock(BlockEntity tile) {
        index = tile.getBlockPos();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        ResourceIdBlock compareId = (ResourceIdBlock) obj;
        return index.equals(compareId.index) && side == compareId.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, side);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        CompoundTag indexNBT = new CompoundTag();
        indexNBT.putInt("x", index.getX());
        indexNBT.putInt("y", index.getY());
        indexNBT.putInt("z", index.getZ());
        nbt.put("index", indexNBT);
        nbt.putByte("side", (byte) (side != null ? side.ordinal() : 0));
    }

    @Override
    protected void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        CompoundTag indexNBT = nbt.getCompound("index");
        index = new BlockPos(indexNBT.getInt("x"), indexNBT.getInt("y"), indexNBT.getInt("z"));
        side = Direction.values()[nbt.getByte("side")];
    }
}
