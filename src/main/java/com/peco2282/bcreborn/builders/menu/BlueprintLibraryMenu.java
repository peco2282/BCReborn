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
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.gui.slots.SlotBase;
import com.peco2282.bcreborn.common.gui.slots.SlotOutput;
import com.peco2282.bcreborn.common.gui.widgets.ScrollbarWidget;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlueprintLibraryMenu extends BuildCraftMenu<BlueprintLibraryMenu> {
    private final BlueprintLibraryBlockEntity library;
    private final ContainerData data;
    protected ScrollbarWidget scrollbarWidget;

    // Client constructor
    public BlueprintLibraryMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, (BlueprintLibraryBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(2));
    }

    // Server constructor
    public BlueprintLibraryMenu(int windowId, Inventory playerInventory, BlueprintLibraryBlockEntity library) {
        this(windowId, playerInventory, library, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> library.progressIn;
                    case 1 -> library.progressOut;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> library.progressIn = value;
                    case 1 -> library.progressOut = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    private BlueprintLibraryMenu(int windowId, Inventory playerInventory, BlueprintLibraryBlockEntity library, ContainerData data) {
        super(BuildersMenuTypes.BLUEPRINT_LIBRARY.get(), windowId, playerInventory);
        this.library = library;
        this.data = data;
        this.addDataSlots(data);

        this.scrollbarWidget = new ScrollbarWidget(163, 21, 244, 0, 110);
        this.scrollbarWidget.hidden = true;
        this.addWidget(scrollbarWidget);

        // Library slots (Adjusted positions for 244x220)
        this.addSlot(new BlueprintLibrarySlot(library, 0, 219, 57, playerInventory.player));
        this.addSlot(new SlotOutput(library.inv, 1, 175, 57));
        this.addSlot(new BlueprintLibrarySlot(library, 2, 175, 79, playerInventory.player));
        this.addSlot(new SlotOutput(library.inv, 3, 219, 79));

        // Player inventory
        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 9; k1++) {
                this.addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 138 + l * 18));
            }
        }

        // Hotbar
        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 196));
        }
    }

    public int getProgressIn() {
        return data.get(0);
    }

    public int getProgressOut() {
        return data.get(1);
    }

    public ScrollbarWidget getScrollbarWidget() {
        return scrollbarWidget;
    }

    public BlueprintLibraryBlockEntity getLibrary() {
        return library;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return library.stillValid(player);
    }


    static class BlueprintLibrarySlot extends SlotBase {
        private BlueprintLibraryBlockEntity library;
        private Player player;
        private int slot;
        public BlueprintLibrarySlot(BlueprintLibraryBlockEntity entity, int slotIndex, int posX, int posY, Player player) {
            super(entity, slotIndex, posX, posY);
            this.library = entity;
            this.player = player;
            this.slot = slotIndex;
        }

        @Override
        public void setChanged() {
            if (slot == 0) {
                library.uploadingPlayer = player;
            } else if (slot == 2) {
                library.downloadingPlayer = player;
            }
            this.container.setChanged();
        }
    }
}
