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

import java.util.Arrays;
import com.peco2282.bcreborn.robotics.map.MapWorld;
import com.peco2282.bcreborn.robotics.map.MapManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.api.items.IMapLocation;
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

/**
 * Port of the original {@code TileZonePlan}: sixteen painted zones (one per colour), a map location in slot 0 is
 * written with the selected zone and the planner's name into slot 1, and a map location with a zone put into
 * slot 2 is loaded into the selected zone.
 */
public class ZonePlanBlockEntity extends BuildCraftBlockEntity implements MenuProvider, Container {

  public static final int RESOLUTION = 2048;
  public static final int CRAFT_TIME = 120;
  private static final int RESOLUTION_CHUNKS = RESOLUTION >> 4;
  /** Slots: map to write, written map, map to import. */
  public static final int SLOT_INPUT = 0;
  public static final int SLOT_OUTPUT = 1;
  public static final int SLOT_IMPORT = 2;
  private final SimpleInventory inv = new SimpleInventory(3, "inv", 64);
  private final ZonePlan[] selectedAreas = new ZonePlan[16];
  public int chunkStartX, chunkStartZ;
  public short progress = 0;
  public String mapName = "";
  private int currentSelectedArea = 0;
  /** Original {@code previewColors}: 10 x 8 map colours around the planner, drawn on its front. */
  private static final int PREVIEW_BLOCKS_PER_PIXEL = 10;
  private static final int PREVIEW_INTERVAL = 100;
  private final byte[] previewColors = new byte[80];
  private boolean previewChanged = true;

  public ZonePlanBlockEntity(BlockPos pos, BlockState state) {
    super(RoboticsBlockEntityTypes.ZONE_PLAN.get(), pos, state);
  }

  @Override
  public void initialize() {
    super.initialize();

    int cx = getBlockPos().getX() >> 4;
    int cz = getBlockPos().getZ() >> 4;

    chunkStartX = cx - RESOLUTION_CHUNKS / 2;
    chunkStartZ = cz - RESOLUTION_CHUNKS / 2;
  }

  /** Whether the block column is inside the area this planner maps (original: RESOLUTION blocks around it). */
  public boolean isInMapArea(int x, int z) {
    int ix = x - (chunkStartX << 4);
    int iz = z - (chunkStartZ << 4);
    return ix >= 0 && iz >= 0 && ix < RESOLUTION && iz < RESOLUTION;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if ((level.getGameTime() + pos.asLong()) % PREVIEW_INTERVAL == 0) {
      recalculatePreview();
    }

    // Original updateEntity(): writing a map takes CRAFT_TIME ticks.
    ItemStack input = inv.getItem(SLOT_INPUT);
    if (!input.isEmpty() && inv.getItem(SLOT_OUTPUT).isEmpty() && input.getItem() instanceof MapLocationItem) {
      if (progress < CRAFT_TIME) {
        progress++;
      } else {
        ItemStack stack = inv.removeItem(SLOT_INPUT, 1);
        ZonePlan zone = selectedAreas[currentSelectedArea];
        if (zone != null) {
          MapLocationItem.setZone(stack, zone);
          ((MapLocationItem) stack.getItem()).setName(stack, Component.literal(mapName));
        }
        inv.setItem(SLOT_OUTPUT, stack);
        progress = 0;
        setChanged();
      }
    } else if (progress != 0) {
      progress = 0;
    }
  }

  /** Original {@code recalculatePreview()}: one map colour per 10 blocks, synced to the client when it changes. */
  private void recalculatePreview() {
    BlockPos pos = getBlockPos();
    int baseX = pos.getX() - Math.floorMod(pos.getX(), PREVIEW_BLOCKS_PER_PIXEL);
    int baseZ = pos.getZ() - Math.floorMod(pos.getZ(), PREVIEW_BLOCKS_PER_PIXEL);
    int half = PREVIEW_BLOCKS_PER_PIXEL / 2;
    MapWorld world = MapManager.getLoaded(level,
      (baseX - 5 * PREVIEW_BLOCKS_PER_PIXEL) >> 4, (baseZ - 4 * PREVIEW_BLOCKS_PER_PIXEL) >> 4,
      (baseX + 5 * PREVIEW_BLOCKS_PER_PIXEL) >> 4, (baseZ + 4 * PREVIEW_BLOCKS_PER_PIXEL) >> 4);
    if (world == null) return;
    byte[] colors = new byte[previewColors.length];
    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 10; x++) {
        int bx = baseX + x * PREVIEW_BLOCKS_PER_PIXEL - 5 * PREVIEW_BLOCKS_PER_PIXEL + half;
        int bz = baseZ + y * PREVIEW_BLOCKS_PER_PIXEL - 4 * PREVIEW_BLOCKS_PER_PIXEL + half;
        colors[y * 10 + x] = world.hasChunk(bx >> 4, bz >> 4) ? (byte) world.getColor(bx, bz) : 0;
      }
    }
    if (!Arrays.equals(colors, previewColors)) {
      System.arraycopy(colors, 0, previewColors, 0, colors.length);
      setChanged();
      level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }
  }

  /** Client: the preview colours once after they changed (or always with {@code force}), otherwise null. */
  @Nullable
  public byte[] getPreviewTexture(boolean force) {
    if (previewChanged || force) {
      previewChanged = false;
      return previewColors;
    }
    return null;
  }

  /** Original {@code importMap()}: a map that holds a zone replaces the selected zone. */
  private void importMap(ItemStack stack) {
    if (stack.getItem() instanceof IMapLocation map && map.getZone(stack) instanceof ZonePlan zone) {
      selectedAreas[currentSelectedArea] = zone;
      setChanged();
      if (level instanceof ServerLevel serverLevel) {
        for (ServerPlayer player : serverLevel.players()) {
          if (player.containerMenu instanceof ZonePlanMenu menu && menu.getTile() == this) {
            BCNetworkManager.sendSyncZonePlanAreaLoaded(player, getBlockPos(), zone);
          }
        }
      }
    }
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    nbt.putString("name", mapName);
    nbt.putByte("currentSelectedArea", (byte) currentSelectedArea);
    nbt.putByteArray("preview", previewColors);

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
    currentSelectedArea = Math.max(0, Math.min(selectedAreas.length - 1, nbt.getByte("currentSelectedArea")));
    byte[] preview = nbt.getByteArray("preview");
    if (preview.length == previewColors.length) {
      System.arraycopy(preview, 0, previewColors, 0, preview.length);
      previewChanged = true;
    }

    inv.readTag(nbt.getCompound("inv"));

    for (int i = 0; i < selectedAreas.length; ++i) {
      if (nbt.contains("selectedArea[" + i + "]")) {
        selectedAreas[i] = new ZonePlan();
        selectedAreas[i].readTag(nbt.getCompound("selectedArea[" + i + "]"));
      }
    }
  }

  /** Original {@code rpcLoadArea()}: the zone of colour {@code index}, which becomes the selected one. */
  public ZonePlan selectArea(int index) {
    currentSelectedArea = index;
    if (selectedAreas[index] == null) {
      selectedAreas[index] = new ZonePlan();
    }
    return selectedAreas[index];
  }

  public int getCurrentSelectedArea() {
    return currentSelectedArea;
  }

  @Nullable
  public ZonePlan getArea(int index) {
    return selectedAreas[index];
  }

  public boolean isValidAreaIndex(int index) {
    return index >= 0 && index < selectedAreas.length;
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
    if (slotId == SLOT_IMPORT && level != null && !level.isClientSide) {
      importMap(itemstack);
    }
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    // Original isItemValidForSlot(): only maps go in, and nothing goes into the output.
    return slot != SLOT_OUTPUT && stack.getItem() instanceof MapLocationItem;
  }

  @Override
  public int getMaxStackSize() {
    return inv.getMaxStackSize();
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
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
  public @Nullable AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
    return new ZonePlanMenu(windowId, inventory, this);
  }

  public void doSetName(String name) {
    mapName = name;
  }
}
