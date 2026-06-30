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
package com.peco2282.bcreborn.robotics.block.entity;

import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ZonePlanBlockEntity extends BuildCraftBlockEntity implements MenuProvider, Container {

  public static final int RESOLUTION = 2048;
  public static final int CRAFT_TIME = 120;
  private static final int PREVIEW_BLOCKS_PER_PIXEL = 10;
  private static final int RESOLUTION_CHUNKS = RESOLUTION >> 4;
  private final byte[] previewColors = new byte[80];
  private final SimpleInventory inv = new SimpleInventory(3, "inv", 64);
  private final SafeTimeTracker previewRecalcTimer = new SafeTimeTracker(100);
  private final ZonePlan[] selectedAreas = new ZonePlan[16];
  public int chunkStartX, chunkStartZ;
  public short progress = 0;
  public String mapName = "";
  private boolean previewColorsPushed = false;

  public ZonePlanBlockEntity(BlockPos pos, BlockState state) {
    super(RoboticsBlockEntityTypes.ZONE_PLAN.get(), pos, state);
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
    inv.writeTag(invNBT);
    nbt.put("inv", invNBT);

    for (int i = 0; i < selectedAreas.length; ++i) {
      if (selectedAreas[i] != null) {
        CompoundTag subNBT = new CompoundTag();
        selectedAreas[i].writeTag(subNBT);
        nbt.put("selectedArea[" + i + "]", subNBT);
      }
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);

    mapName = nbt.getString("name");

    inv.readTag(nbt.getCompound("inv"));

    for (int i = 0; i < selectedAreas.length; ++i) {
      if (nbt.contains("selectedArea[" + i + "]")) {
        selectedAreas[i] = new ZonePlan();
        selectedAreas[i].readTag(nbt.getCompound("selectedArea[" + i + "]"));
      }
    }
  }

  public ZonePlan selectArea(int index) {
    if (selectedAreas[index] == null) {
      selectedAreas[index] = new ZonePlan();
    }
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

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornrobotics.zone_plan");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return null;
  }

  public void doSetName(String name) {
    mapName = name;
  }
}
