/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.builders.menu;

import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.builders.block.entity.ArchitectBlockEntity;
import com.peco2282.bcreborn.common.gui.slots.SlotBase;
import com.peco2282.bcreborn.common.gui.slots.SlotOutput;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArchitectMenu extends BuildCraftMenu<ArchitectMenu> {
    private final ArchitectBlockEntity architect;
    private final ContainerData data;

    // Client constructor
    public ArchitectMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, (ArchitectBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(1));
    }

    // Server constructor
    public ArchitectMenu(int windowId, Inventory playerInventory, ArchitectBlockEntity architect) {
        this(windowId, playerInventory, architect, new ContainerData() {
            @Override
            public int get(int index) {
                if (index == 0) {
                    return architect.getComputingProgressScaled(24);
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    private ArchitectMenu(int windowId, Inventory playerInventory, ArchitectBlockEntity architect, ContainerData data) {
        super(BuildersMenuTypes.ARCHITECT.get(), windowId, playerInventory);
        this.architect = architect;
        this.data = data;
        this.addDataSlots(data);

        // Architect slots
        this.addSlot(new ArchitectSlot(architect, playerInventory.player, 0, 135, 35));
        this.addSlot(new SlotOutput(architect.getInventory(), 1, 194, 35));

        // Player inventory
        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 9; k1++) {
                this.addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 88 + k1 * 18, 84 + l * 18));
            }
        }

        // Hotbar
        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 88 + i1 * 18, 142));
        }
    }

    public int getComputingProgress() {
        return data.get(0);
    }

    public ArchitectBlockEntity getArchitect() {
        return architect;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return architect.stillValid(player);
    }

    static class ArchitectSlot extends SlotBase {
        private ArchitectBlockEntity architect;
        private Player player;
        private int slot;
        public ArchitectSlot(ArchitectBlockEntity architect, Player player, int index, int x, int y) {
            super(architect.getInventory(), index, x, y);
            this.architect = architect;
            this.player = player;
            this.slot = index;
        }

        @Override
        public void setChanged() {
            if (slot == 0) {
                architect.currentAuthorName = player.getDisplayName().getString();
            }

            this.container.setChanged();
        }
    }
}
