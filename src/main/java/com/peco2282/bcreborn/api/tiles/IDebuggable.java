package com.peco2282.bcreborn.api.tiles;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IDebuggable {
    void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player);
}
