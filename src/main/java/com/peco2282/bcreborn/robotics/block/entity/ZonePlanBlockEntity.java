/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.block.entity;

import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.robotics.BlockEntityTypesRobotics;
import com.peco2282.bcreborn.robotics.ZonePlan;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ZonePlanBlockEntity extends BuildCraftBlockEntity implements Container {

    public static final int RESOLUTION = 2048;
    public static final int CRAFT_TIME = 120;
    private static final int PREVIEW_BLOCKS_PER_PIXEL = 10;
    private static int RESOLUTION_CHUNKS = RESOLUTION >> 4;

    public int chunkStartX, chunkStartZ;
    public short progress = 0;
    public String mapName = "";

    private final byte[] previewColors = new byte[80];
    private final SimpleInventory inv = new SimpleInventory(3, "inv", 64);
    private final SafeTimeTracker previewRecalcTimer = new SafeTimeTracker(100);

    private boolean previewColorsPushed = false;
    private ZonePlan[] selectedAreas = new ZonePlan[16];
    private int currentSelectedArea = 0;

    public ZonePlanBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesRobotics.ZONE_PLAN.get(), pos, state);
    }

    public byte[] getPreviewTexture(boolean force) {
        if (!previewColorsPushed || force) {
            previewColorsPushed = true;
            return previewColors;
        }
        return null;
    }

    @Override
    public void initialize() {
        super.initialize();

        int cx = getBlockPos().getX() >> 4;
        int cz = getBlockPos().getZ() >> 4;

        chunkStartX = cx - RESOLUTION_CHUNKS / 2;
        chunkStartZ = cz - RESOLUTION_CHUNKS / 2;
    }

    @Override
    protected void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }

        if (previewRecalcTimer.markTimeIfDelay(level)) {
            recalculatePreview();
        }

        // 1.7.10 logic for crafting MapLocation
        ItemStack stack0 = inv.getItem(0);
        ItemStack stack1 = inv.getItem(1);

        // TODO: ItemMapLocation check and crafting logic porting
        /*
        if (!stack0.isEmpty() && stack1.isEmpty() && stack0.getItem() instanceof ItemMapLocation) {
            if (progress < CRAFT_TIME) {
                progress++;
                if (level.getGameTime() % 5 == 0) {
                    // sendNetworkUpdate();
                }
            } else {
                ItemStack stack = inv.removeItem(0, 1);
                if (selectedAreas[currentSelectedArea] != null) {
                    ItemMapLocation.setZone(stack, selectedAreas[currentSelectedArea]);
                    ((INamedItem) stack.getItem()).setName(stack, mapName);
                }
                inv.setItem(1, stack);
            }
        } else if (progress != 0) {
            progress = 0;
            // sendNetworkUpdate();
        }
        */
    }

    private void recalculatePreview() {
        byte[] newPreviewColors = new byte[80];
        // TODO: BuildCraftRobotics.manager integration
        /*
        MapWorld mw = BCRebornRobotics.getManager().getWorld(level);
        BlockPos pos = getBlockPos();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 10; x++) {
                int tx = (x * PREVIEW_BLOCKS_PER_PIXEL) - (5 * PREVIEW_BLOCKS_PER_PIXEL) + (PREVIEW_BLOCKS_PER_PIXEL / 2);
                int ty = (y * PREVIEW_BLOCKS_PER_PIXEL) - (4 * PREVIEW_BLOCKS_PER_PIXEL) + (PREVIEW_BLOCKS_PER_PIXEL / 2);
                newPreviewColors[y * 10 + x] = (byte) mw.getColor(pos.getX() - (pos.getX() % PREVIEW_BLOCKS_PER_PIXEL) + tx, pos.getZ() - (pos.getZ() % PREVIEW_BLOCKS_PER_PIXEL) + ty);
            }
        }

        if (!Arrays.equals(previewColors, newPreviewColors)) {
            System.arraycopy(newPreviewColors, 0, previewColors, 0, 80);
            // sendNetworkUpdate();
        }
        */
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("name", mapName);

        CompoundTag invNBT = new CompoundTag();
        inv.write(invNBT);
        nbt.put("inv", invNBT);

        for (int i = 0; i < selectedAreas.length; ++i) {
            if (selectedAreas[i] != null) {
                CompoundTag subNBT = new CompoundTag();
                selectedAreas[i].writeToNBT(subNBT);
                nbt.put("selectedArea[" + i + "]", subNBT);
            }
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        mapName = nbt.getString("name");
        if (mapName == null) {
            mapName = "";
        }

        inv.read(nbt.getCompound("inv"));

        for (int i = 0; i < selectedAreas.length; ++i) {
            if (nbt.contains("selectedArea[" + i + "]")) {
                selectedAreas[i] = new ZonePlan();
                selectedAreas[i].readFromNBT(nbt.getCompound("selectedArea[" + i + "]"));
            }
        }
    }

    public ZonePlan selectArea(int index) {
        if (selectedAreas[index] == null) {
            selectedAreas[index] = new ZonePlan();
        }
        currentSelectedArea = index;
        return selectedAreas[index];
    }

    public void setArea(int index, ZonePlan area) {
        selectedAreas[index] = area;
    }

    @Override
    public int getContainerSize() {
        return inv.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inv.isEmpty();
    }

    @Override
    public ItemStack getItem(int slotId) {
        return inv.getItem(slotId);
    }

    @Override
    public ItemStack removeItem(int slotId, int count) {
        return inv.removeItem(slotId, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slotId) {
        return inv.removeItemNoUpdate(slotId);
    }

    @Override
    public void setItem(int slotId, ItemStack itemstack) {
        inv.setItem(slotId, itemstack);
        // TODO: importMap logic
    }

    @Override
    public int getMaxStackSize() {
        return inv.getMaxStackSize();
    }

    @Override
    public boolean stillValid(Player player) {
        return super.stillValid(player);
    }

    @Override
    public void clearContent() {
        inv.clearContent();
    }
}
