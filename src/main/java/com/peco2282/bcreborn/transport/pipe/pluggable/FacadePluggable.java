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
import com.peco2282.bcreborn.api.serialization.NbtReader;
import com.peco2282.bcreborn.api.serialization.NbtWriter;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.item.FacadeItem.FacadeState;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Port of the original {@code FacadePluggable}. A basic facade has one state. A phased facade has a default state
 * and one state per pipe wire; the first state whose wire is active is shown, otherwise the default one (original
 * {@code TileGenericPipe.refreshRenderState()}). A state without a block is transparent: nothing is drawn.
 */
public class FacadePluggable extends PipePluggable<FacadePluggable> {
  private static final FacadeState EMPTY = new FacadeState(Blocks.AIR.defaultBlockState(), null, false);

  private FacadeState[] states = {EMPTY};
  private int active;

  public FacadePluggable() {
    super(BCRebornTransport.FACADE);
  }

  public FacadePluggable(BlockState state) {
    this(state, false);
  }

  public FacadePluggable(BlockState state, boolean hollow) {
    this(new FacadeState[]{new FacadeState(state, null, hollow)});
  }

  public FacadePluggable(FacadeState[] states) {
    super(BCRebornTransport.FACADE);
    if (states.length > 0) this.states = states.clone();
    this.active = defaultState();
  }

  /** The state without a wire, or the first one. */
  private int defaultState() {
    for (int i = 0; i < states.length; i++) {
      if (states[i].wire() == null) return i;
    }
    return 0;
  }

  private FacadeState activeState() {
    return states[Math.max(0, Math.min(active, states.length - 1))];
  }

  public FacadeState[] getStates() {
    return states.clone();
  }

  /** The block shown at the moment; air while the active state is transparent. */
  public BlockState getState() {
    BlockState state = activeState().state();
    return state != null ? state : Blocks.AIR.defaultBlockState();
  }

  /** Original {@code isTransparent()}: the active state shows nothing. */
  public boolean isTransparent() {
    return activeState().state() == null;
  }

  public boolean isHollow() {
    return activeState().hollow();
  }

  public void setHollow(boolean hollow) {
    FacadeState state = activeState();
    states[Math.max(0, Math.min(active, states.length - 1))] = new FacadeState(state.state(), state.wire(), hollow);
  }

  /** Picks the state for the pipe's wire signals; a change is synced and may open or close the side. */
  @Override
  public void update(IPipeBlockEntity pipe, Direction direction) {
    if (states.length < 2 || !(pipe instanceof PipeBlockEntity entity)) return;
    if (entity.getLevel() == null || entity.getLevel().isClientSide) return;
    int wanted = defaultState();
    for (int i = 0; i < states.length; i++) {
      PipeWire wire = states[i].wire();
      if (wire != null && entity.isWireActive(wire)) {
        wanted = i;
        break;
      }
    }
    if (wanted != active) {
      active = wanted;
      entity.onPluggableChanged();
    }
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    ListTag list = new ListTag();
    for (FacadeState state : states) {
      CompoundTag tag = new CompoundTag();
      state.writeToNbt(tag);
      list.add(tag);
    }
    nbt.put("states", list);
    nbt.putByte("active", (byte) active);
  }

  @Override
  public void readTag(CompoundTag nbt) {
    if (nbt.contains("states", Tag.TAG_LIST)) {
      ListTag list = nbt.getList("states", Tag.TAG_COMPOUND);
      if (!list.isEmpty()) {
        states = new FacadeState[list.size()];
        for (int i = 0; i < states.length; i++) states[i] = FacadeState.fromNbt(list.getCompound(i));
      }
      active = Math.max(0, Math.min(nbt.getByte("active"), states.length - 1));
      return;
    }
    // Older saves: one block state and a hollow flag.
    BlockState[] state = {Blocks.AIR.defaultBlockState()};
    boolean[] hollow = {false};
    NbtReader.of(nbt)
      .applyBlockState("state", value -> state[0] = value)
      .applyBoolean("hollow", value -> hollow[0] = value);
    states = new FacadeState[]{new FacadeState(state[0], null, hollow[0])};
    active = 0;
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    CompoundTag tag = new CompoundTag();
    writeTag(tag);
    data.writeNbt(tag);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    CompoundTag tag = data.readNbt();
    if (tag != null) readTag(tag);
  }

  @Override
  public ItemStack[] getDropItems(IPipeBlockEntity pipe) {
    ItemStack stack = new ItemStack(TransportItems.FACADE.get());
    TransportItems.FACADE.get().setFacadeStates(stack, states.clone());
    return new ItemStack[]{stack};
  }

  @Override
  public boolean isBlocking(IPipeBlockEntity pipe, Direction direction) {
    return !isHollow();
  }

  @Override
  public boolean isSolidOnSide(IPipeBlockEntity pipe, Direction direction) {
    return !isTransparent() && !isHollow();
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    float min = 0.0f;
    float max = 1.0f;
    float thickness = 1.0f / 16.0f;

    return switch (side) {
      case DOWN -> new AABB(min, 0, min, max, thickness, max);
      case UP -> new AABB(min, 1.0f - thickness, min, max, 1.0, max);
      case NORTH -> new AABB(min, min, 0, max, max, thickness);
      case SOUTH -> new AABB(min, min, 1.0f - thickness, max, max, 1.0);
      case WEST -> new AABB(0, min, min, thickness, max, max);
      case EAST -> new AABB(1.0f - thickness, min, min, 1.0, max, max);
    };
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return FacadePluggableRenderer.INSTANCE;
  }
}
