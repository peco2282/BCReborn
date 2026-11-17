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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Port of the original {@code PlugPluggable}: closes one side of a pipe, so nothing connects there (pipes,
 * inventories and pipe wires alike). It has no state of its own.
 */
public class PlugPluggable extends PipePluggable<PlugPluggable> {

  public PlugPluggable() {
    super(BCRebornTransport.PLUG);
  }

  @Override
  public void writeTag(CompoundTag nbt) {
  }

  @Override
  public void readTag(CompoundTag nbt) {
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
  }

  @Override
  public void readData(FriendlyByteBuf data) {
  }

  @Override
  public ItemStack[] getDropItems(IPipeBlockEntity pipe) {
    return new ItemStack[]{new ItemStack(TransportItems.PIPE_PLUG.get())};
  }

  @Override
  public boolean isBlocking(IPipeBlockEntity pipe, Direction direction) {
    return true;
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    // Original: 0.25..0.75 wide, 0.125..0.251 from the face.
    return PluggableBoxes.box(side, 0.25f, 0.75f, 0.125f, 0.251f);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return PlugPluggableRenderer.INSTANCE;
  }
}
