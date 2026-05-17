package com.peco2282.bcreborn.api.transport.pluggable;

import net.minecraft.world.level.block.Block;

public interface IFacadePluggable {
    Block getCurrentBlock();

    int getCurrentMetadata();

    boolean isTransparent();

    boolean isHollow();
}
