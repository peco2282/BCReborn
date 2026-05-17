package com.peco2282.bcreborn.api.blocks;


import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IColorRemovable {
	boolean removeColorFromBlock(Level world, int x, int y, int z, Direction side);
}
