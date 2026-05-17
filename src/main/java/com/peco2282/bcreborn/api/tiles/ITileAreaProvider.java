package com.peco2282.bcreborn.api.tiles;

import com.peco2282.bcreborn.api.core.IAreaProvider;
import net.minecraft.core.BlockPos;

public interface ITileAreaProvider extends IAreaProvider {
    boolean isValidFromLocation(BlockPos pos);
}
