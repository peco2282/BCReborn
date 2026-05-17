package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IPipePluggableItem {
    PipePluggable createPipePluggable(IPipe pipe, Direction side, ItemStack stack);
}
