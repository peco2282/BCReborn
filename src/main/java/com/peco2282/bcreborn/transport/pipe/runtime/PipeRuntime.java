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
package com.peco2282.bcreborn.transport.pipe.runtime;

import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.EnergyPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviourManager;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.ItemTransportModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Owner of a pipe's mutable transport state.
 *
 * <p>The block entity is deliberately kept as the Minecraft/Forge adapter. This class owns
 * transport selection, ticking and persistence so transports can later be hosted outside a
 * block entity without duplicating the pipe rules.</p>
 */
public final class PipeRuntime {
  private final PipeBlockEntity host;
  private final ItemTransportModule itemTransport;
  private final EnumMap<Direction, SimpleInventory> filters = new EnumMap<>(Direction.class);
  private final boolean[] wireSignals = new boolean[4];

  private PipeType type;
  private PipeMaterial material;
  private PipeBehaviour behaviour;
  @Nullable private FluidTransportModule fluidTransport;
  @Nullable private EnergyTransportModule energyTransport;
  @Nullable private FluidTank fluidTank;
  @Nullable private EnergyStorage energyStorage;

  private int ticksSincePull;
  private int fluidRoundRobinIndex;
  private Direction ironPipeOutput = Direction.UP;
  private int ironPipeEnergyLimit = 1280;
  private Direction extractionSide = Direction.DOWN;
  private int filterSlotIndex;
  private long usedFilters;
  @Nullable private DyeColor pipeColor;
  private PipeBlockEntity.ExtractFilterMode extractFilterMode = PipeBlockEntity.ExtractFilterMode.WHITE_LIST;

  public PipeRuntime(PipeBlockEntity host, PipeType type, PipeMaterial material) {
    this.host = host;
    this.itemTransport = new ItemTransportModule(host);
    for (Direction direction : Direction.values()) {
      filters.put(direction, new SimpleInventory(9, "Filter " + direction.name(), 1));
    }
    reconfigure(type, material);
  }

  private void reconfigure(PipeType type, PipeMaterial material) {
    if (material.unsupports(type)) {
      throw new IllegalArgumentException("Pipe material does not support the specified pipe type");
    }
    this.type = type;
    this.material = material;
    fluidTank = type == PipeType.FLUID ? new FluidTank(1000) : null;
    int capacity = type == PipeType.ENERGY ? material.getEnergyTransferRate() * 2 : -1;
    energyStorage = type == PipeType.ENERGY
        ? new EnergyStorage(capacity, capacity, capacity, 0)
        : null;
    fluidTransport = type == PipeType.FLUID ? new FluidTransportModule(host) : null;
    energyTransport = type == PipeType.ENERGY ? new EnergyTransportModule(host, material) : null;
    behaviour = PipeBehaviourManager.getBehaviour(type, material);
  }

  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (type == PipeType.ITEM) itemTransport.tick(level, pos);
      return;
    }
    ticksSincePull++;
    if (behaviour != null) behaviour.tick(host, level, pos, state);
    switch (type) {
      case ITEM -> itemTransport.tick(level, pos);
      case FLUID -> {
        if (fluidTransport != null) fluidTransport.tick(level, pos);
      }
      case ENERGY -> {
        if (behaviour instanceof EnergyPipeBehaviour energyBehaviour) {
          energyBehaviour.extractEnergy(host);
        }
        if (energyTransport != null) energyTransport.tick(level, pos);
      }
    }
  }

  public void load(CompoundTag tag) {
    ticksSincePull = tag.getInt("ticksSincePull");
    if (tag.contains("Wires")) {
      byte[] wires = tag.getByteArray("Wires");
      for (int i = 0; i < wireSignals.length && i < wires.length; i++) wireSignals[i] = wires[i] != 0;
    }
    PipeType loadedType = tag.contains("TransportType")
        ? PipeType.valueOf(tag.getString("TransportType")) : type;
    PipeMaterial loadedMaterial = tag.contains("PipeMaterial")
        ? PipeMaterial.valueOf(tag.getString("PipeMaterial").toUpperCase()) : material;
    if (loadedType != type || loadedMaterial != material) reconfigure(loadedType, loadedMaterial);

    if (tag.contains("IronPipeEnergyLimit")) ironPipeEnergyLimit = tag.getInt("IronPipeEnergyLimit");
    if (tag.contains("IronPipeOutput")) ironPipeOutput = Direction.from3DDataValue(tag.getInt("IronPipeOutput"));
    if (tag.contains("ExtractionSide")) extractionSide = Direction.from3DDataValue(tag.getInt("ExtractionSide"));
    if (tag.contains("usedFilters")) usedFilters = tag.getLong("usedFilters");
    pipeColor = tag.contains("PipeColor") ? DyeColor.byId(tag.getInt("PipeColor")) : null;
    if (tag.contains("ExtractFilterMode")) {
      int ordinal = tag.getInt("ExtractFilterMode");
      if (ordinal >= 0 && ordinal < PipeBlockEntity.ExtractFilterMode.values().length) {
        extractFilterMode = PipeBlockEntity.ExtractFilterMode.values()[ordinal];
      }
    }
    if (tag.contains("Filters")) {
      ListTag filtersTag = tag.getList("Filters", Tag.TAG_COMPOUND);
      for (int i = 0; i < filtersTag.size(); i++) {
        CompoundTag entry = filtersTag.getCompound(i);
        Direction direction = entry.contains("Dir")
            ? Direction.from3DDataValue(entry.getInt("Dir"))
            : Direction.from3DDataValue(i);
        SimpleInventory inventory = filters.get(direction);
        if (inventory != null) inventory.readTag(entry);
      }
    }
    itemTransport.load(tag);
    if (energyTransport != null) energyTransport.load(tag);
    if (fluidTransport != null) fluidTransport.load(tag);
    if (fluidTank != null && tag.contains("Fluid")) fluidTank.readFromNBT(tag.getCompound("Fluid"));
    if (energyStorage != null && tag.contains("Energy")) energyStorage.read(tag.getCompound("Energy"));
  }

  public void save(CompoundTag tag) {
    tag.putInt("ticksSincePull", ticksSincePull);
    tag.putString("TransportType", type.name());
    tag.putString("PipeMaterial", material.name());
    tag.putInt("IronPipeOutput", ironPipeOutput.get3DDataValue());
    tag.putInt("IronPipeEnergyLimit", ironPipeEnergyLimit);
    tag.putInt("ExtractionSide", extractionSide.get3DDataValue());
    ListTag filtersTag = new ListTag();
    for (Direction direction : Direction.values()) {
      CompoundTag filterTag = new CompoundTag();
      filterTag.putInt("Dir", direction.get3DDataValue());
      filters.get(direction).writeTag(filterTag);
      filtersTag.add(filterTag);
    }
    tag.put("Filters", filtersTag);
    tag.putLong("usedFilters", usedFilters);
    if (pipeColor != null) tag.putInt("PipeColor", pipeColor.getId());
    tag.putInt("ExtractFilterMode", extractFilterMode.ordinal());
    byte[] wires = new byte[wireSignals.length];
    for (int i = 0; i < wireSignals.length; i++) wires[i] = (byte) (wireSignals[i] ? 1 : 0);
    tag.putByteArray("Wires", wires);
    itemTransport.save(tag);
    if (energyTransport != null) energyTransport.save(tag);
    if (fluidTransport != null) fluidTransport.save(tag);
    if (fluidTank != null) {
      CompoundTag fluid = new CompoundTag();
      fluidTank.writeToNBT(fluid);
      tag.put("Fluid", fluid);
    }
    if (energyStorage != null) {
      CompoundTag energy = new CompoundTag();
      energyStorage.write(energy);
      tag.put("Energy", energy);
    }
  }

  public void setWireSignal(DyeColor color, boolean signal) {
    int index = wireIndex(color);
    if (index < 0 || wireSignals[index] == signal) return;
    wireSignals[index] = signal;
    Set<BlockPos> visited = new HashSet<>();
    visited.add(host.getBlockPos());
    propagateWireSignal(color, signal, visited);
    host.setChanged();
  }

  private void propagateWireSignal(DyeColor color, boolean signal, Set<BlockPos> visited) {
    Level level = host.getLevel();
    if (level == null) return;
    int index = wireIndex(color);
    for (Direction direction : Direction.values()) {
      BlockPos neighborPos = host.getBlockPos().relative(direction);
      if (!visited.add(neighborPos) || !level.isLoaded(neighborPos)) continue;
      BlockEntity blockEntity = level.getBlockEntity(neighborPos);
      if (!(blockEntity instanceof PipeBlockEntity neighbor)) continue;
      PipeRuntime neighborRuntime = neighbor.getRuntime();
      if (neighborRuntime.wireSignals[index] != signal) {
        neighborRuntime.wireSignals[index] = signal;
        neighbor.setChanged();
        neighborRuntime.propagateWireSignal(color, signal, visited);
      }
    }
  }

  private static int wireIndex(@Nullable DyeColor color) {
    if (color == null) return -1;
    return switch (color) {
      case RED -> 0;
      case BLUE -> 1;
      case YELLOW -> 2;
      case GREEN -> 3;
      default -> -1;
    };
  }

  public PipeType type() { return type; }
  public PipeMaterial material() { return material; }
  public PipeBehaviour behaviour() { return behaviour; }
  public void setBehaviour(PipeBehaviour behaviour) { this.behaviour = behaviour; }
  public ItemTransportModule itemTransport() { return itemTransport; }
  @Nullable public FluidTransportModule fluidTransport() { return fluidTransport; }
  @Nullable public EnergyTransportModule energyTransport() { return energyTransport; }
  @Nullable public FluidTank fluidTank() { return fluidTank; }
  @Nullable public EnergyStorage energyStorage() { return energyStorage; }
  public int ticksSincePull() { return ticksSincePull; }
  public void resetTicksSincePull() { ticksSincePull = 0; }
  public int fluidRoundRobinIndex() { return fluidRoundRobinIndex; }
  public void advanceFluidRoundRobin(int size) { if (size > 0) fluidRoundRobinIndex = (fluidRoundRobinIndex + 1) % size; }
  public Direction ironPipeOutput() { return ironPipeOutput; }
  public void setIronPipeOutput(Direction value) { ironPipeOutput = value; }
  public int ironPipeEnergyLimit() { return ironPipeEnergyLimit; }
  public void setIronPipeEnergyLimit(int value) { ironPipeEnergyLimit = value; }
  public Direction extractionSide() { return extractionSide; }
  public void setExtractionSide(Direction value) { extractionSide = value; }
  public int filterSlotIndex() { return filterSlotIndex; }
  public void setFilterSlotIndex(int value) { filterSlotIndex = value; }
  public Map<Direction, SimpleInventory> filters() { return Collections.unmodifiableMap(filters); }
  public SimpleInventory filter(Direction direction) { return filters.get(direction); }
  public long usedFilters() { return usedFilters; }
  public void setUsedFilters(long value) { usedFilters = value; }
  @Nullable public DyeColor pipeColor() { return pipeColor; }
  public void setPipeColor(@Nullable DyeColor value) { pipeColor = value; }
  public PipeBlockEntity.ExtractFilterMode extractFilterMode() { return extractFilterMode; }
  public void setExtractFilterMode(PipeBlockEntity.ExtractFilterMode value) { extractFilterMode = value; }
  public List<TravelingItem> travelingItems() { return itemTransport.getTravelingItems(); }
  public void injectItem(ItemStack stack, Direction from, float speed) { itemTransport.injectItem(stack, from, speed); }
  public void dropItems() { itemTransport.dropItems(); }
}
