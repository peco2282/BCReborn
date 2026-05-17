package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.core.Direction;

public interface IPipePluggableRenderer {
    void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, int renderPass, int x, int y, int z);
}
