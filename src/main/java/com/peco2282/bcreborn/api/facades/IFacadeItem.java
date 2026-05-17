/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.facades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface IFacadeItem {
    FacadeType getFacadeType(ItemStack facade);

    ItemStack getFacadeForBlock(Block block, int meta);

    Block[] getBlocksForFacade(ItemStack facade);

    int[] getMetaValuesForFacade(ItemStack facade);
}
