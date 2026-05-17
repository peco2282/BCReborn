/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Use the template system to describe fillers
 */
public class Template extends BlueprintBase {

    public Template() {
        id.extension = "tpl";
    }

    public Template(int sizeX, int sizeY, int sizeZ) {
        super(sizeX, sizeY, sizeZ);
        id.extension = "tpl";
    }

    @Override
    public void readFromWorld(IBuilderContext context, BlockEntity anchorTile, int x, int y, int z) {
        int posX = (int) (x - context.surroundingBox().pMin().x);
        int posY = (int) (y - context.surroundingBox().pMin().y);
        int posZ = (int) (z - context.surroundingBox().pMin().z);

        BlockPos pos = new BlockPos(x, y, z);
        if (!context.world().isEmptyBlock(pos)) {
            put(posX, posY, posZ, new SchematicMask(true));
        }
    }

    @Override
    public void saveContents(CompoundTag nbt) {
        // Note: this way of storing data is suboptimal, we really need a bit
        // per mask entry, not a byte. However, this is fine, as compression
        // will fix it.
        byte[] data = new byte[sizeX * sizeY * sizeZ];
        int ind = 0;

        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    data[ind] = (byte) ((get(x, y, z) == null) ? 0 : 1);
                    ind++;
                }
            }
        }

        nbt.putByteArray("mask", data);
    }

    @Override
    public void loadContents(CompoundTag nbt) throws BptError {
        byte[] data = nbt.getByteArray("mask");
        int ind = 0;

        for (int x = 0; x < sizeX; ++x) {
            for (int y = 0; y < sizeY; ++y) {
                for (int z = 0; z < sizeZ; ++z) {
                    if (data[ind] == 1) {
                        put(x, y, z, new SchematicMask(true));
                    }
                    ind++;
                }
            }
        }
    }

    @Override
    public ItemStack getStack() {
        // Item reference will be resolved at runtime via registry
        return ItemStack.EMPTY;
    }
}
