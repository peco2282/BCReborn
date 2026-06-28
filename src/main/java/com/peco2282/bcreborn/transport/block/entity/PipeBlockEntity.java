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
package com.peco2282.bcreborn.transport.block.entity;

import com.peco2282.bcreborn.api.blocks.IColoredBlock;
import com.peco2282.bcreborn.api.gates.IGate;
import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.transport.TransportBlockEntityTypes;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
import com.peco2282.bcreborn.transport.pipe.behaviour.EnergyPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviourManager;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.ItemTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.PipeEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Forge BlockEntity lifecycle holder.
 * <p>
 * Responsibilities:
 * - NBT serialization
 * - Capability exposure
 * - tick delegation
 * - module ownership
 * </p>
 * Transport logic itself is delegated to transport modules.
 */
public class PipeBlockEntity extends BuildCraftBlockEntity implements IColoredBlock, IPipeBlockEntity, Container, IDebuggable {
  public final SideProperties sideProperties = new SideProperties();
  // アイテム輸送
  private final ItemTransportModule itemTransportModule = new ItemTransportModule(this);
  // 流体輸送モジュール（FLUID パイプのみ有効）
  @Nullable
  private FluidTransportModule fluidTransportModule;
  // エネルギー輸送モジュール（ENERGY パイプのみ有効）
  @Nullable
  private EnergyTransportModule energyTransportModule;
  // 流体ストレージ（FLUID パイプのみ生成）
  @Nullable
  private FluidTank fluidTank;
  // エネルギーストレージ（ENERGY パイプのみ生成）
  @Nullable
  private EnergyStorage energyStorage;
  private final EnumMap<Direction, SimpleInventory> filters = new EnumMap<>(Direction.class);
  // Capability lazy optionals
  private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> new PipeItemHandler(this, null));
  private LazyOptional<IFluidHandler> fluidHandlerCap = LazyOptional.empty();
  private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();
  @SuppressWarnings("unchecked")
  private final LazyOptional<IEnergyStorage>[] energySideCaps = new LazyOptional[6];
  private final boolean[] wireSignals = new boolean[4];
  private PipeType transportType;
  private PipeMaterial pipeMaterial;
  private int ticksSincePull = 0;
  // 流体流入方向を記録するためのラッパー（逆流防止用）
  @Nullable
  private PipeFluidHandler pipeFluidHandler = null;
  // Clay流体パイプのラウンドロビンカウンタ
  private int fluidRoundRobinIndex = 0;
  private PipeBehaviour behaviour;
  private Direction ironPipeOutput = Direction.UP;
  private int ironPipeEnergyLimit = 1280;
  private Direction extractionSide = Direction.DOWN;
  private int filterSlotIndex = 0;
  // Diamond Pipe: 使用済みフィルタースロットのbitmask（方向ordinal * 9 + slotIndex）
  private long usedFilters = 0L;
  // Lapis Pipe: パイプの色（0〜15、EnumColor互換）
//  private int pipeColor = 0;
  @Nullable // if null will be no color
  private DyeColor pipeColor = null;
  private ExtractFilterMode extractFilterMode = ExtractFilterMode.WHITE_LIST;

  public PipeBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, PipeType.ITEM, PipeMaterial.IRON);
  }

  public PipeBlockEntity(BlockPos pos, BlockState state, PipeType type, PipeMaterial material) {
    super(getBlockEntityType(type), pos, state);
    initPipe(type, material);
  }

  private void initPipe(PipeType type, PipeMaterial material) {
    if (material.unsupports(type)) {
      throw new IllegalArgumentException("Pipe material does not support the specified pipe type");
    }
    this.transportType = type;
    this.pipeMaterial = material;
    this.fluidTank = (type == PipeType.FLUID) ? new FluidTank(1000) : null;
    int energyCapVal = (type == PipeType.ENERGY) ? material.getEnergyTransferRate() * 2 : -1;
    this.energyStorage = (type == PipeType.ENERGY) ? new EnergyStorage(energyCapVal, energyCapVal, energyCapVal, 0) : null;
    this.fluidTransportModule = (type == PipeType.FLUID) ? new FluidTransportModule(this) : null;
    this.energyTransportModule = (type == PipeType.ENERGY) ? new EnergyTransportModule(this) : null;
    if (fluidTank != null) {
      var handler = new PipeFluidHandler(this, fluidTank);
      this.fluidHandlerCap = LazyOptional.of(() -> handler);
      this.pipeFluidHandler = handler;
    } else {
      this.fluidHandlerCap = LazyOptional.empty();
    }
    this.energyCap = (energyStorage != null) ? LazyOptional.of(() -> energyStorage) : LazyOptional.empty();
    if (energyTransportModule != null) {
      for (Direction dir : Direction.values()) {
        energySideCaps[dir.ordinal()] = LazyOptional.of(() -> new PipeEnergyStorage(energyTransportModule, dir));
      }
    } else {
      Arrays.fill(energySideCaps, LazyOptional.empty());
    }
    for (Direction dir : Direction.values()) {
      filters.putIfAbsent(dir, new SimpleInventory(9, "Filter " + dir.name(), 1));
    }
    this.behaviour = PipeBehaviourManager.getBehaviour(type, material);

    // Powered pipe (for extract)
    if (material == PipeMaterial.WOOD && (type == PipeType.ITEM || type == PipeType.FLUID)) {
      this.setBattery(new EnergyStorage(1024, 64, 64, 0));
    }
  }

  public static BlockEntityType<PipeBlockEntity> getBlockEntityType(PipeType type) {
    return switch (type) {
      case ITEM -> TransportBlockEntityTypes.ITEM_PIPE.get();
      case FLUID -> TransportBlockEntityTypes.FLUID_PIPE.get();
      case ENERGY -> TransportBlockEntityTypes.ENERGY_PIPE.get();
    };
  }

  public int getTicksSincePull() {
    return ticksSincePull;
  }

  public void resetTicksSincePull() {
    ticksSincePull = 0;
  }

  public PipeBehaviour getBehaviour() {
    return behaviour;
  }

  public void setBehaviour(PipeBehaviour behaviour) {
    this.behaviour = behaviour;
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (transportType == PipeType.ITEM) {
        tickItems(level, pos);
      }
      return;
    }
    ticksSincePull++;

    if (behaviour != null) {
      behaviour.tick(this, level, pos, state);
    }

    if (transportType == PipeType.ITEM) {
      tickItems(level, pos);
      if (!itemTransportModule.getTravelingItems().isEmpty()) {
        level.sendBlockUpdated(pos, state, state, 3);
      }
    } else if (transportType == PipeType.FLUID) {
      if (fluidTransportModule != null) {
        fluidTransportModule.tick(level, pos);
      }
    } else if (transportType == PipeType.ENERGY) {
      // エネルギー吸い出し（Wood パイプ等）を先に実行してからモジュールをtick
      if (behaviour instanceof EnergyPipeBehaviour eb) {
        eb.extractEnergy(this);
      }
      if (energyTransportModule != null) {
        energyTransportModule.tick(level, pos);
      }
    }
  }

  // ---- アイテム輸送 ----

  private void tickItems(Level level, BlockPos pos) {
    itemTransportModule.tick(level, pos);
  }

  public int getExtractionEnergy() {
    if (getBattery() != null) {
      return getBattery().getEnergyStored();
    }
    return 0;
  }

  public void consumeExtractionEnergy(int amount) {
    if (getBattery() != null) {
      getBattery().useEnergy(0, amount, false);
    }
  }

  public void injectItem(ItemStack stack, Direction from) {
    injectItemWithSpeed(stack, from, pipeMaterial.getItemSpeed());
  }

  public void injectItemWithSpeed(ItemStack stack, Direction from, float speed) {
    itemTransportModule.injectItem(stack, from, speed);
  }

  public void dropItems() {
    itemTransportModule.dropItems();
  }

  public List<TravelingItem> getTravelingItems() {
    return itemTransportModule.getTravelingItems();
  }

  // ---- Fluid ----

  @Nullable
  public FluidTank getFluidTank() {
    return fluidTank;
  }

  @Nullable
  public FluidTransportModule getFluidTransportModule() {
    return fluidTransportModule;
  }

  public int getFluidRoundRobinIndex() {
    return fluidRoundRobinIndex;
  }

  public void advanceFluidRoundRobin(int size) {
    if (size > 0) fluidRoundRobinIndex = (fluidRoundRobinIndex + 1) % size;
  }

  // ---- Energy ----

  @Nullable
  public EnergyTransportModule getEnergyTransportModule() {
    return energyTransportModule;
  }

  @Nullable
  public EnergyStorage getPipeEnergyStorage() {
    return energyStorage;
  }

  public int getPipeEnergyStored() {
    return energyStorage != null ? energyStorage.getEnergyStored() : 0;
  }

  // ---- Wire signals ----

  public void setWireSignal(DyeColor color, boolean signal) {
    int index = getWireIndex(color);
    if (index >= 0 && wireSignals[index] != signal) {
      wireSignals[index] = signal;
      Set<BlockPos> visited = new HashSet<>();
      visited.add(worldPosition);
      propagateWireSignal(color, signal, visited);
      setChanged();
    }
  }

  /**
   * visited Set を共有しながら再帰 DFS でワイヤー信号を伝播する。
   * A→B→C→A のようなループも visited により安全に停止する。
   * unloaded chunk は level.isLoaded() でスキップする。
   */
  private void propagateWireSignal(DyeColor color, boolean signal, Set<BlockPos> visited) {
    if (level == null) return;
    int index = getWireIndex(color);
    if (index < 0) return;

    for (Direction dir : Direction.values()) {
      BlockPos neighborPos = worldPosition.relative(dir);
      if (!visited.add(neighborPos)) continue;          // 既訪問ならスキップ
      if (!level.isLoaded(neighborPos)) continue;       // unloaded chunk をスキップ

      BlockEntity be = level.getBlockEntity(neighborPos);
      if (!(be instanceof PipeBlockEntity neighbor)) continue;

      if (neighbor.wireSignals[index] != signal) {
        neighbor.wireSignals[index] = signal;
        neighbor.setChanged();
        neighbor.propagateWireSignal(color, signal, visited);
      }
    }
  }

  private int getWireIndex(@Nullable DyeColor color) {
    if (color == null) return -1;
    return switch (color) {
      case RED -> 0;
      case BLUE -> 1;
      case YELLOW -> 2;
      case GREEN -> 3;
      default -> -1;
    };
  }

  // ---- Getters / Setters ----

  public PipeType getTransportType() {
    return transportType;
  }

  public void setTransportType(PipeType transportType) {
    this.transportType = transportType;
    setChanged();
  }

  public PipeMaterial getPipeMaterial() {
    return pipeMaterial;
  }

  public void setPipeMaterial(PipeMaterial pipeMaterial) {
    this.pipeMaterial = pipeMaterial;
    setChanged();
  }

  public int getIronPipeEnergyLimit() {
    return ironPipeEnergyLimit;
  }

  public void setIronPipeEnergyLimit(int ironPipeEnergyLimit) {
    this.ironPipeEnergyLimit = ironPipeEnergyLimit;
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public Direction getIronPipeOutput() {
    return ironPipeOutput;
  }

  public void setIronPipeOutput(Direction ironPipeOutput) {
    this.ironPipeOutput = ironPipeOutput;
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public Direction getExtractionSide() {
    return extractionSide;
  }

  public void setExtractionSide(Direction extractionSide) {
    this.extractionSide = extractionSide;
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public int getFilterSlotIndex() {
    return filterSlotIndex;
  }

  public void setFilterSlotIndex(int filterSlotIndex) {
    this.filterSlotIndex = filterSlotIndex;
  }

  public Map<Direction, SimpleInventory> getFilters() {
    return Collections.unmodifiableMap(filters);
  }

  public SimpleInventory getFilter(Direction direction) {
    return filters.get(direction);
  }

  public long getUsedFilters() {
    return usedFilters;
  }

  public void setUsedFilters(long usedFilters) {
    this.usedFilters = usedFilters;
    setChanged();
  }

  public @Nullable DyeColor getPipeColor() {
    return pipeColor;
  }

  public void setPipeColor(@Nullable DyeColor color) {
    this.pipeColor = color;
    setChanged();
    Level level = getLevel();
    BlockPos pos = getBlockPos();
    if (!level.isClientSide) {
      level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }
  }

  public ExtractFilterMode getExtractFilterMode() {
    return extractFilterMode;
  }

  public void setExtractFilterMode(ExtractFilterMode extractFilterMode) {
    this.extractFilterMode = extractFilterMode;
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  // ---- NBT ----

  // --- Container Implementation ---
  @Override
  public int getContainerSize() {
    return 9; // Filtered Buffer holds 9 slots
  }

  @Override
  public boolean isEmpty() {
    for (int i = 0; i < getContainerSize(); i++) {
      if (!getItem(i).isEmpty()) return false;
    }
    return true;
  }

  @Override
  public ItemStack getItem(int p_18941_) {
    // We reuse filters as storage for simplicity in this port,
    // though ideally Filtered Buffer should have its own inventory.
    return getFilter(Direction.UP).getItem(p_18941_);
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    return getFilter(Direction.UP).removeItem(p_18942_, p_18943_);
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18944_) {
    return getFilter(Direction.UP).removeItemNoUpdate(p_18944_);
  }

  @Override
  public void setItem(int p_18945_, ItemStack p_18946_) {
    getFilter(Direction.UP).setItem(p_18945_, p_18946_);
    setChanged();
  }

  @Override
  public boolean stillValid(Player p_18946_) {
    return true;
  }

  @Override
  public void clearContent() {
    getFilter(Direction.UP).clearContent();
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    ticksSincePull = tag.getInt("ticksSincePull");
    if (tag.contains("Wires")) {
      byte[] wires = tag.getByteArray("Wires");
      for (int i = 0; i < wireSignals.length && i < wires.length; i++) {
        wireSignals[i] = wires[i] != 0;
      }
    }
    PipeType oldType = this.transportType;
    PipeMaterial oldMaterial = this.pipeMaterial;
    if (tag.contains("TransportType")) {
      this.transportType = PipeType.valueOf(tag.getString("TransportType"));
    }
    if (tag.contains("PipeMaterial")) {
      this.pipeMaterial = PipeMaterial.valueOf(tag.getString("PipeMaterial").toUpperCase());
    }
    if (this.transportType != oldType || this.pipeMaterial != oldMaterial) {
      initPipe(this.transportType, this.pipeMaterial);
    }
    if (tag.contains("IronPipeEnergyLimit")) {
      this.ironPipeEnergyLimit = tag.getInt("IronPipeEnergyLimit");
    }
    if (tag.contains("IronPipeOutput")) {
      this.ironPipeOutput = Direction.from3DDataValue(tag.getInt("IronPipeOutput"));
    }
    if (tag.contains("ExtractionSide")) {
      this.extractionSide = Direction.from3DDataValue(tag.getInt("ExtractionSide"));
    }
    if (tag.contains("usedFilters")) {
      this.usedFilters = tag.getLong("usedFilters");
    }
    if (tag.contains("PipeColor")) {
      this.pipeColor = DyeColor.byId(tag.getInt("PipeColor"));
    }
    if (tag.contains("ExtractFilterMode")) {
      this.extractFilterMode = ExtractFilterMode.values()[tag.getInt("ExtractFilterMode")];
    }
    if (tag.contains("Filters")) {
      ListTag filtersTag = tag.getList("Filters", Tag.TAG_COMPOUND);
      for (int i = 0; i < filtersTag.size(); i++) {
        CompoundTag entry = filtersTag.getCompound(i);
        if (entry.contains("Dir")) {
          Direction dir = Direction.from3DDataValue(entry.getInt("Dir"));
          filters.get(dir).readTag(entry);
        } else if (i < 6) {
          // legacy fallback: index-based
          filters.get(Direction.from3DDataValue(i)).readTag(entry);
        }
      }
    }
    itemTransportModule.load(tag);
    if (energyTransportModule != null) {
      energyTransportModule.load(tag);
    }
    if (fluidTransportModule != null) {
      fluidTransportModule.load(tag);
    }
    if (fluidTank != null && tag.contains("Fluid")) {
      fluidTank.readFromNBT(tag.getCompound("Fluid"));
    }
    if (energyStorage != null && tag.contains("Energy")) {
      energyStorage.read(tag.getCompound("Energy"));
    }
    if (tag.contains("SideProperties")) {
      sideProperties.readFromNBT(tag.getCompound("SideProperties"));
    }
  }
  // --- End Container Implementation ---

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putInt("ticksSincePull", ticksSincePull);
    tag.putString("TransportType", transportType.name());
    tag.putString("PipeMaterial", pipeMaterial.name());
    tag.putInt("IronPipeOutput", ironPipeOutput.get3DDataValue());
    tag.putInt("IronPipeEnergyLimit", ironPipeEnergyLimit);
    tag.putInt("ExtractionSide", extractionSide.get3DDataValue());

    ListTag filtersTag = new ListTag();
    for (Direction dir : Direction.values()) {
      CompoundTag filterTag = new CompoundTag();
      filterTag.putInt("Dir", dir.get3DDataValue());
      filters.get(dir).writeTag(filterTag);
      filtersTag.add(filterTag);
    }
    tag.put("Filters", filtersTag);
    tag.putLong("usedFilters", usedFilters);
    if (pipeColor != null)
      tag.putInt("PipeColor", pipeColor.getId());
    tag.putInt("ExtractFilterMode", extractFilterMode.ordinal());

    byte[] wires = new byte[wireSignals.length];
    for (int i = 0; i < wireSignals.length; i++) {
      wires[i] = (byte) (wireSignals[i] ? 1 : 0);
    }
    tag.putByteArray("Wires", wires);

    itemTransportModule.save(tag);
    if (energyTransportModule != null) {
      energyTransportModule.save(tag);
    }
    if (fluidTransportModule != null) {
      fluidTransportModule.save(tag);
    }

    if (fluidTank != null) {
      CompoundTag fluidTag = new CompoundTag();
      fluidTank.writeToNBT(fluidTag);
      tag.put("Fluid", fluidTag);
    }
    if (energyStorage != null) {
      var energy = new CompoundTag();
      energyStorage.write(energy);
      tag.put("Energy", energy);
    }
    CompoundTag sideTag = new CompoundTag();
    sideProperties.writeToNBT(sideTag);
    tag.put("SideProperties", sideTag);
  }

  // ---- Capabilities ----

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER && transportType == PipeType.ITEM) {
      return itemHandlerCap.cast();
    }
    if (cap == ForgeCapabilities.FLUID_HANDLER && transportType == PipeType.FLUID) {
      if (pipeFluidHandler != null && side != null) {
        pipeFluidHandler.setFillDirection(side);
      }
      return fluidHandlerCap.cast();
    }
    if (cap == ForgeCapabilities.ENERGY && transportType == PipeType.ENERGY) {
      if (side != null) {
        return energySideCaps[side.ordinal()].cast();
      }
      return energyCap.cast();
    }
    // Powered pipe (for extract)
    if (cap == ForgeCapabilities.ENERGY && pipeMaterial == PipeMaterial.WOOD) {
      var battery = getBattery();
      if (battery != null) {
        return LazyOptional.of(() -> battery).cast();
      }
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemHandlerCap.invalidate();
    fluidHandlerCap.invalidate();
    energyCap.invalidate();
    for (var cap : energySideCaps) {
      if (cap != null) cap.invalidate();
    }
  }

  // ---- Network ----

  @Nullable
  public Item getPipeItem() {
    RegistryObject<PipeBlock> block = TransportBlocks.PIPES.get(transportType, pipeMaterial);
    if (block != null) {
      return block.get().asItem();
    }
    return Items.AIR;
  }

  @Nullable
  public PipePluggable<?> getPipePluggable(Direction direction) {
    return sideProperties.pluggables[direction.ordinal()];
  }

  public void setPipePluggable(Direction direction, PipePluggable<?> pluggable) {
    PipePluggable<?> old = sideProperties.pluggables[direction.ordinal()];
    if (old == pluggable) return;

    if (old != null) {
      old.onDetachedPipe(this, direction);
    }

    sideProperties.pluggables[direction.ordinal()] = pluggable;

    pluggable.onAttachedPipe(this, direction);

    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  private final IPipe pipeApi = new IPipe() {
    @Override
    public IPipeBlockEntity getBlockEntity() {
      return PipeBlockEntity.this;
    }

    @Override
    @Nullable
    public IGate getGate(Direction side) {
      return null;
    }

    @Override
    public boolean hasGate(Direction side) {
      return false;
    }

    @Override
    public boolean isWired(PipeWire wire) {
      return false;
    }

    @Override
    public boolean isWireActive(PipeWire wire) {
      return false;
    }
  };

  public boolean hasPipePluggable(Direction direction) {
    return sideProperties.pluggables[direction.ordinal()] != null;
  }

  @Override
  public boolean hasBlockingPluggable(Direction direction) {
    PipePluggable<?> p = getPipePluggable(direction);
    return p != null && p.isBlocking(this, direction);
  }

  @Override
  public void scheduleNeighborChange() {
  }

  @Override
  public void scheduleRenderUpdate() {
  }

  @Override
  public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from) {
    return injectItem(stack, doAdd, from, null);
  }

  @Override
  public PipeType getPipeType() {
    return switch (transportType) {
      case ITEM -> PipeType.ITEM;
      case FLUID -> PipeType.FLUID;
      case ENERGY -> PipeType.ENERGY;
    };
  }

  @Override
  public Level getWorld() {
    return getLevel();
  }

  @Override
  public BlockPos getPos() {
    return worldPosition;
  }

  @Override
  public boolean isPipeConnected(Direction with) {
    BlockState state = getBlockState();
    if (state.getBlock() instanceof PipeBlock) {
      return state.getValue(PipeBlock.PROPERTY_MAP.get(with));
    }
    return false;
  }

  @Override
  public Block getNeighborBlock(Direction dir) {
    return getLevel().getBlockState(worldPosition.relative(dir)).getBlock();
  }

  @Override
  @Nullable
  public BlockEntity getNeighborBlockEntity(Direction dir) {
    return getLevel().getBlockEntity(worldPosition.relative(dir));
  }

  @Override
  @Nullable
  public IPipe getNeighborPipe(Direction dir) {
    BlockEntity be = getNeighborBlockEntity(dir);
    if (be instanceof PipeBlockEntity other) {
      return other.getPipe();
    }
    return null;
  }

  public IPipe getPipe() {
    return pipeApi;
  }

  public ArrayList<ItemStack> computeItemDrop() {
    ArrayList<ItemStack> list = new ArrayList<>();
    // Pluggables
    for (PipePluggable<?> pluggable : sideProperties.pluggables) {
      if (pluggable != null) {
        Collections.addAll(list, pluggable.getDropItems(this));
      }
    }
    return list;
  }

  @Override
  public boolean recolorBlock(BlockState state, Level level, BlockPos pos, Direction side, DyeColor color) {
    this.pipeColor = color;
    setChanged();
    level.sendBlockUpdated(pos, state, state, 3);
    return true;
  }

  @Override
  public boolean canInjectItems(@Nullable Direction from) {
    return true;
  }

  @Override
  public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from, @Nullable Integer color) {
    if (doAdd) {
      injectItem(stack, from);
    }
    return stack.getCount();
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    info.add("Type      : " + getPipeType().getSerializedName());
    info.add("Material  : " + pipeMaterial.getSerializedName());
    info.add("Color     : " + pipeColor);
    if (getPipeType() == PipeType.ITEM) {
      info.add("Traveling Items");
      itemTransportModule.getTravelingItems().forEach(it -> {
        info.add("  Item: " + it.getStack());
        info.add("  Direction: " + it.getNextDirection().getSerializedName().toUpperCase());
      });
    } else if (getPipeType() == PipeType.FLUID) {
      info.add("Traveling Fluids");
    } else {
      info.add("Energy:");
      info.add(String.format("  Storage : %d / %d RF", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
      info.add(String.format("  Max Rate: %d RF/tick", energyTransportModule.getMaxPower()));
      info.add(String.format("  Received: %.2f RF/tick (%.2f RF/s)", energyTransportModule.getLastTickReceived(), energyTransportModule.getLastTickReceived() * 20));
      info.add(String.format("  Sent    : %.2f RF/tick (%.2f RF/s)", energyTransportModule.getLastTickSent(), energyTransportModule.getLastTickSent() * 20));
      info.add(String.format("  Loss    : %.1f%%", energyTransportModule.getPowerResistance() * 100));
    }
  }

  public enum ExtractFilterMode {
    WHITE_LIST,
    BLACK_LIST,
    ROUND_ROBIN;

    public ExtractFilterMode next() {
      return values()[(this.ordinal() + 1) % values().length];
    }
  }

  public static class SideProperties {
    public PipePluggable<?>[] pluggables = new PipePluggable[Direction.values().length];

    public void writeToNBT(CompoundTag nbt) {
      for (int i = 0; i < Direction.values().length; i++) {
        PipePluggable<?> pluggable = pluggables[i];
        final String key = "pluggable[" + i + "]";
        if (pluggable == null) {
          nbt.remove(key);
        } else {
          CompoundTag pluggableData = new CompoundTag();
          String name = pluggable.getType().id().toString();
          pluggableData.putString("pluggableName", name);

          pluggable.writeToNBT(pluggableData);
          nbt.put(key, pluggableData);
        }
      }
    }

    public void readFromNBT(CompoundTag nbt) {
      for (int i = 0; i < Direction.values().length; i++) {
        final String key = "pluggable[" + i + "]";
        if (!nbt.contains(key)) {
          continue;
        }
        CompoundTag pluggableData = nbt.getCompound(key);
        pluggables[i] = PipeManager.createPipePluggable(pluggableData.getString("pluggableName"), pluggableData);
      }
    }

    public void rotateLeft() {
      PipePluggable<?>[] newPluggables = new PipePluggable[Direction.values().length];
      for (Direction dir : Direction.values()) {
        Direction newDir = dir;
        if (dir.getAxis() != Direction.Axis.Y) {
          newDir = dir.getClockWise();
        }
        newPluggables[newDir.ordinal()] = pluggables[dir.ordinal()];
      }
      pluggables = newPluggables;
    }
  }


}
