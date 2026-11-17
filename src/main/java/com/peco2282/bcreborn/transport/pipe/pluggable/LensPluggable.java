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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code LensPluggable}. A lens paints every item that passes through its side; a filter lets
 * only items of its colour through (see {@link LensFilterHandler}). Neither blocks the connection.
 * <p>
 * A null colour is the original "clear" variant: a clear lens removes the paint, a clear filter matches unpainted
 * items.
 */
public class LensPluggable extends PipePluggable<LensPluggable> {
  /** Saved colour value of the clear variant (original: -1). */
  private static final byte CLEAR = -1;

  @Nullable
  private DyeColor color;
  private boolean filter;

  public LensPluggable() {
    super(BCRebornTransport.LENS);
  }

  public LensPluggable(@Nullable DyeColor color, boolean filter) {
    this();
    this.color = color;
    this.filter = filter;
  }

  /** Original {@code eventHandler(PipeEventItem.Entered)}: a lens on the side the item came in through paints it. */
  public static void paintEntering(PipeBlockEntity pipe, TravelingItem item) {
    paint(pipe, item, item.getEntryDirection());
  }

  /** Original {@code eventHandler(PipeEventItem.ReachedEnd)}: a lens on the side the item leaves through paints it. */
  public static void paintLeaving(PipeBlockEntity pipe, TravelingItem item) {
    paint(pipe, item, item.getNextDirection());
  }

  private static void paint(PipeBlockEntity pipe, TravelingItem item, @Nullable Direction side) {
    if (side != null && pipe.getPipePluggable(side) instanceof LensPluggable lens && !lens.filter) {
      item.setColor(lens.color);
    }
  }

  @Nullable
  public DyeColor getColor() {
    return color;
  }

  public boolean isFilter() {
    return filter;
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    nbt.putByte("c", color == null ? CLEAR : (byte) color.getId());
    nbt.putBoolean("f", filter);
  }

  @Override
  public void readTag(CompoundTag nbt) {
    byte id = nbt.getByte("c");
    color = id < 0 ? null : DyeColor.byId(id);
    filter = nbt.getBoolean("f");
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    data.writeByte(color == null ? CLEAR : color.getId());
    data.writeBoolean(filter);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    byte id = data.readByte();
    color = id < 0 ? null : DyeColor.byId(id);
    filter = data.readBoolean();
  }

  @Override
  public ItemStack[] getDropItems(IPipeBlockEntity pipe) {
    return new ItemStack[]{new ItemStack(TransportItems.lens(color, filter))};
  }

  @Override
  public boolean isBlocking(IPipeBlockEntity pipe, Direction direction) {
    return false;
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    // Original: 0.1875..0.8125 wide, 0.125 thick at the end of the arm.
    return PluggableBoxes.box(side, 0.1875f, 0.8125f, 0.0f, 0.125f);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return LensPluggableRenderer.INSTANCE;
  }
}
