package com.peco2282.bcreborn.api.transport;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IInjectable {
    boolean canInjectItems(Direction from);

    /**
     * Offers an ItemStack for addition to the pipe. Will be rejected if the
     * pipe doesn't accept items from that side.
     *
     * @param stack ItemStack offered for addition. Do not manipulate this!
     * @param doAdd If false no actual addition should take place. Implementors should simulate.
     * @param from Orientation the ItemStack is offered from.
     * @param color The color of the item to be added to the pipe, or null for no color.
     * @return Amount of items used from the passed stack.
     */
    int injectItem(ItemStack stack, boolean doAdd, Direction from, Integer color);
}
