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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.gates.Gate;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.PipeWire;

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
  public static final int MAX_WIRE_STRENGTH = 255;

  private final PipeBlockEntity host;
  private final ItemTransportModule itemTransport;
  private final EnumMap<Direction, SimpleInventory> filters = new EnumMap<>(Direction.class);
  /** Original {@code Pipe.wireSet}: which wire colours are installed (indexed by {@link PipeWire#ordinal()}). */
  private final boolean[] wireSet = new boolean[PipeWire.VALUES.length];
  /** Original {@code Pipe.wireSignalStrength}: 255 at a broadcasting gate, -1 per pipe; not saved, recomputed. */
  private final int[] wireSignalStrength = new int[PipeWire.VALUES.length];
  /** Recompute every installed wire's network on the next server tick (after load, placement or shape change). */
  private boolean wiresDirty = true;

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
  // Equivalent to the original PipeLogicIron.lastPower. Not saved to NBT (initialized from the current signal on initialize)
  @Nullable private Boolean ironLastRedstonePower;
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
    if (!level.isClientSide) {
      tickWires();
    }
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
    // "Wires" holds the installed wire colours (PipeWire order). Older saves used the same key for a flag that
    // stripes pipes set when "installing" a wire, so it is read as installed wires as well.
    if (tag.contains("Wires")) {
      byte[] wires = tag.getByteArray("Wires");
      for (int i = 0; i < wireSet.length && i < wires.length; i++) wireSet[i] = wires[i] != 0;
    }
    if (tag.contains("WireSignals")) {
      // Client sync only; the server always recomputes strengths.
      byte[] active = tag.getByteArray("WireSignals");
      for (int i = 0; i < wireSignalStrength.length && i < active.length; i++) wireSignalStrength[i] = active[i] != 0 ? 1 : 0;
    }
    wiresDirty = true;
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
    byte[] wires = new byte[wireSet.length];
    for (int i = 0; i < wireSet.length; i++) wires[i] = (byte) (wireSet[i] ? 1 : 0);
    tag.putByteArray("Wires", wires);
    writeWireSignals(tag);
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

  // ---- Pipe wires (original Pipe.wireSet / wireSignalStrength / updateSignalState) ----

  public boolean isWired(PipeWire wire) {
    return wireSet[wire.ordinal()];
  }

  public boolean isWireActive(PipeWire wire) {
    return wireSignalStrength[wire.ordinal()] > 0;
  }

  public int wireSignalStrength(PipeWire wire) {
    return wireSignalStrength[wire.ordinal()];
  }

  /**
   * Original {@code BlockGenericPipe.addWire()}.
   *
   * @return false if the wire was already installed
   */
  public boolean addWire(PipeWire wire) {
    if (wireSet[wire.ordinal()]) return false;
    wireSet[wire.ordinal()] = true;
    wireSignalStrength[wire.ordinal()] = 0;
    updateWireNetwork(wire);
    host.setChanged();
    return true;
  }

  /**
   * Original {@code BlockGenericPipe.stripWire()}: removes the wire and lets the rest of the network recompute.
   *
   * @return false if the wire was not installed
   */
  public boolean removeWire(PipeWire wire) {
    if (!wireSet[wire.ordinal()]) return false;
    wireSet[wire.ordinal()] = false;
    wireSignalStrength[wire.ordinal()] = 0;
    for (Direction dir : Direction.values()) {
      PipeBlockEntity neighbor = wireNeighbor(dir, wire);
      if (neighbor != null) neighbor.getRuntime().updateWireNetwork(wire);
    }
    host.setChanged();
    return true;
  }

  public void markWiresDirty() {
    wiresDirty = true;
  }

  /** Server tick: recompute after load / placement / shape changes (original initialize -> updateSignalState). */
  private void tickWires() {
    if (!wiresDirty) return;
    wiresDirty = false;
    for (PipeWire wire : PipeWire.VALUES) {
      if (wireSet[wire.ordinal()]) updateWireNetwork(wire);
    }
  }

  /**
   * Recomputes the signal strength of every pipe in this pipe's {@code wire} network.
   * <p>
   * The original updated strengths with recursive neighbour updates. Here the connected network is collected with
   * a breadth-first search ({@code Set<BlockPos> visited}, {@code level.isLoaded} guard) and the strengths are
   * assigned by distance from the broadcasting pipes: 255 at a source, one less per pipe, never below 0. The
   * result is the same, and switching one source off can no longer cut the signal of another source.
   */
  public void updateWireNetwork(PipeWire wire) {
    Level level = host.getLevel();
    if (level == null || level.isClientSide) return;
    int index = wire.ordinal();
    if (!wireSet[index]) return;

    List<PipeBlockEntity> network = new ArrayList<>();
    Set<BlockPos> visited = new HashSet<>();
    ArrayDeque<PipeBlockEntity> queue = new ArrayDeque<>();
    visited.add(host.getBlockPos());
    queue.add(host);
    while (!queue.isEmpty()) {
      PipeBlockEntity current = queue.poll();
      network.add(current);
      for (Direction dir : Direction.values()) {
        BlockPos next = current.getBlockPos().relative(dir);
        if (visited.contains(next)) continue;
        PipeBlockEntity neighbor = current.getRuntime().wireNeighbor(dir, wire);
        if (neighbor != null) {
          visited.add(next);
          queue.add(neighbor);
        }
      }
    }

    // Multi-source BFS from the broadcasting pipes, staying inside the network.
    Map<BlockPos, Integer> strength = new HashMap<>();
    ArrayDeque<PipeBlockEntity> frontier = new ArrayDeque<>();
    for (PipeBlockEntity pipe : network) {
      if (pipe.getRuntime().isBroadcasting(wire)) {
        strength.put(pipe.getBlockPos(), MAX_WIRE_STRENGTH);
        frontier.add(pipe);
      }
    }
    while (!frontier.isEmpty()) {
      PipeBlockEntity current = frontier.poll();
      int next = strength.get(current.getBlockPos()) - 1;
      if (next <= 0) continue;
      for (Direction dir : Direction.values()) {
        PipeBlockEntity neighbor = current.getRuntime().wireNeighbor(dir, wire);
        if (neighbor != null && !strength.containsKey(neighbor.getBlockPos())) {
          strength.put(neighbor.getBlockPos(), next);
          frontier.add(neighbor);
        }
      }
    }

    for (PipeBlockEntity pipe : network) {
      int value = strength.getOrDefault(pipe.getBlockPos(), 0);
      PipeRuntime runtime = pipe.getRuntime();
      boolean wasActive = runtime.wireSignalStrength[index] > 0;
      runtime.wireSignalStrength[index] = value;
      runtime.wiresDirty = false;
      if (wasActive != (value > 0)) {
        // Original: scheduleRenderUpdate; also lets gates on that pipe see the new trigger state.
        pipe.setChanged();
      }
    }
  }

  private boolean isBroadcasting(PipeWire wire) {
    IPipe pipe = host.getPipe();
    for (Direction side : Direction.values()) {
      if (pipe.getGate(side) instanceof Gate gate && (gate.broadcastSignal & (1 << wire.ordinal())) != 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Original {@code Pipe.isWireConnectedTo()}: the neighbour is a loaded pipe with the same wire, the two pipes are
   * connected to each other and no blocking pluggable sits between them.
   */
  @Nullable
  private PipeBlockEntity wireNeighbor(Direction dir, PipeWire wire) {
    Level level = host.getLevel();
    if (level == null) return null;
    BlockPos pos = host.getBlockPos().relative(dir);
    if (!level.isLoaded(pos) || !(level.getBlockEntity(pos) instanceof PipeBlockEntity neighbor)) return null;
    if (!neighbor.getRuntime().wireSet[wire.ordinal()]) return null;
    if (!PipeBlock.isConnected(host.getBlockState(), dir) || !PipeBlock.isConnected(neighbor.getBlockState(), dir.getOpposite())) {
      return null;
    }
    if (host.hasBlockingPluggable(dir) || neighbor.hasBlockingPluggable(dir.getOpposite())) return null;
    return neighbor;
  }

  /** Client sync of which wires are active, for rendering. */
  public void writeWireSignals(CompoundTag tag) {
    byte[] active = new byte[wireSignalStrength.length];
    for (int i = 0; i < active.length; i++) active[i] = (byte) (wireSignalStrength[i] > 0 ? 1 : 0);
    tag.putByteArray("WireSignals", active);
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
  @Nullable public Boolean ironLastRedstonePower() { return ironLastRedstonePower; }
  public void setIronLastRedstonePower(@Nullable Boolean value) { ironLastRedstonePower = value; }
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
