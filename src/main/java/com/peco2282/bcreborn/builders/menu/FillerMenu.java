package com.peco2282.bcreborn.builders.menu;

import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FillerMenu extends BuildCraftMenu<FillerMenu> {
    private final FillerBlockEntity filler;
    private final ContainerData data;

    // Client constructor
    public FillerMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, (FillerBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()), new SimpleContainerData(1));
    }

    // Server constructor
    public FillerMenu(int windowId, Inventory playerInventory, FillerBlockEntity filler) {
        this(windowId, playerInventory, filler, new ContainerData() {
            @Override
            public int get(int index) {
                if (index == 0) {
                    return filler.currentPattern;
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    filler.currentPattern = value;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    private FillerMenu(int windowId, Inventory playerInventory, FillerBlockEntity filler, ContainerData data) {
        super(BuildersMenuTypes.FILLER.get(), windowId, playerInventory);
        this.filler = filler;
        this.data = data;
        this.addDataSlots(data);

        // Filler inventory
        for (int k = 0; k < 3; k++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(filler.getInventory(), j1 + k * 9, 8 + j1 * 18, 18 + k * 18));
            }
        }

        // Player inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        // Hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    public int getCurrentPattern() {
        return data.get(0);
    }

    public FillerBlockEntity getFiller() {
        return filler;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return filler.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        return transferStackInSlot(player, index);
    }
}
