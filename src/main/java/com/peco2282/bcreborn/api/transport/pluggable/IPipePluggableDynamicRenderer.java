package com.peco2282.bcreborn.api.transport.pluggable;

import com.peco2282.bcreborn.api.transport.IPipe;
import net.minecraft.core.Direction;

public interface IPipePluggableDynamicRenderer {
    void renderPluggable(IPipe pipe, Direction side, PipePluggable pipePluggable, double x, double y, double z);
}
