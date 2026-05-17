package com.peco2282.bcreborn.api.items;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.IZone;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMapLocation extends INamedItem {
    enum MapLocationType {
        CLEAN, SPOT, AREA, PATH, ZONE
    }

    MapLocationType getType(ItemStack stack);

    BlockIndex getPoint(ItemStack stack);

    IBox getBox(ItemStack stack);

    IZone getZone(ItemStack stack);

    List<BlockIndex> getPath(ItemStack stack);

    Direction getPointSide(ItemStack stack);
}
