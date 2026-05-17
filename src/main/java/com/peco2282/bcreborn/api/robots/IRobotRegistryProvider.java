package com.peco2282.bcreborn.api.robots;

import net.minecraft.world.level.Level;

public interface IRobotRegistryProvider {
    IRobotRegistry getRegistry(Level world);
}
