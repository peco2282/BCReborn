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
import com.peco2282.bcreborn.transport.pipe.behaviour.PipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.runtime.PipeRuntime;
import com.peco2282.bcreborn.transport.pipe.transport.EnergyTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import com.peco2282.bcreborn.transport.pipe.transport.PipeEnergyStorage;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Forge BlockEntity lifecycle holder.
 * <p>
 * Responsibilities:
 * - Minecraft/Forge lifecycle and network serialization entry points
 * - Capability exposure
 * - tick delegation
 * - pluggable hosting
 * </p>
 * Pipe state and transport selection are owned by {@link PipeRuntime}.
 */
public class PipeBlockEntity extends BuildCraftBlockEntity implements IColoredBlock, IPipeBlockEntity, Container, IDebuggable {
  public final SideProperties sideProperties = new SideProperties();
  private final PipeRuntime runtime;
  // Capability lazy optionals
  private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> new PipeItemHandler(this, null));
  private final Map<Direction, LazyOptional<IEnergyStorage>> energySideCapsMap = new EnumMap<>(Direction.class);
  private final IPipe pipeApi = new IPipe() {
    @Override
    public IPipeBlockEntity getBlockEntity() {
      return PipeBlockEntity.this;
    }

    @Override
    @Nullable
    public IGate getGate(Direction side) {
      if (getPipePluggable(side) instanceof GatePluggable gate) {
        gate.onAttachedPipe(PipeBlockEntity.this, side);
        return gate.realGate;
      }
      return null;
    }

    @Override
    public boolean hasGate(Direction side) {
      return getGate(side) != null;
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
  private LazyOptional<IFluidHandler> fluidHandlerCap = LazyOptional.empty();
  private final Map<Direction, LazyOptional<IFluidHandler>> fluidSideCaps = new EnumMap<>(Direction.class);
  private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();

  public PipeBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, PipeType.ITEM, PipeMaterial.IRON);
  }

  public PipeBlockEntity(BlockPos pos, BlockState state, PipeType type, PipeMaterial material) {
    super(getBlockEntityType(type), pos, state);
    runtime = new PipeRuntime(this, type, material);
    rebuildCapabilities();
    configureExtractionBattery();
  }

  public static BlockEntityType<PipeBlockEntity> getBlockEntityType(PipeType type) {
    return switch (type) {
      case ITEM -> TransportBlockEntityTypes.ITEM_PIPE.get();
      case FLUID -> TransportBlockEntityTypes.FLUID_PIPE.get();
      case ENERGY -> TransportBlockEntityTypes.ENERGY_PIPE.get();
    };
  }

  private void rebuildCapabilities() {
    fluidHandlerCap.invalidate();
    fluidSideCaps.values().forEach(LazyOptional::invalidate);
    fluidSideCaps.clear();
    energyCap.invalidate();
    energySideCapsMap.values().forEach(LazyOptional::invalidate);
    energySideCapsMap.clear();
    FluidTank fluidTank = runtime.fluidTank();
    EnergyStorage energyStorage = runtime.energyStorage();
    EnergyTransportModule energyTransportModule = runtime.energyTransport();
    if (fluidTank != null) {
      var handler = new PipeFluidHandler(this, fluidTank);
      this.fluidHandlerCap = LazyOptional.of(() -> handler);
      for (Direction dir : Direction.values()) {
        PipeFluidHandler sidedHandler = new PipeFluidHandler(this, fluidTank, dir);
        fluidSideCaps.put(dir, LazyOptional.of(() -> sidedHandler));
      }
    } else {
      this.fluidHandlerCap = LazyOptional.empty();
    }
    this.energyCap = (energyStorage != null) ? LazyOptional.of(() -> energyStorage) : LazyOptional.empty();
    if (energyTransportModule != null) {
      for (Direction dir : Direction.values()) {
        energySideCapsMap.put(dir, LazyOptional.of(() -> new PipeEnergyStorage(energyTransportModule, dir)));
      }
    } else {
      for (Direction dir : Direction.values()) {
        energySideCapsMap.put(dir, LazyOptional.empty());
      }
    }
  }

  private void configureExtractionBattery() {
    boolean needsBattery = runtime.material() == PipeMaterial.WOOD
        && (runtime.type() == PipeType.ITEM || runtime.type() == PipeType.FLUID);
    if (needsBattery && getBattery() == null) {
      setBattery(new EnergyStorage(1024, 64, 64, 0));
    } else if (!needsBattery) {
      setBattery(null);
    }
  }

  public PipeRuntime getRuntime() {
    return runtime;
  }

  public int getTicksSincePull() {
    return runtime.ticksSincePull();
  }

  public void resetTicksSincePull() {
    runtime.resetTicksSincePull();
  }

  public PipeBehaviour getBehaviour() {
    return runtime.behaviour();
  }

  public void setBehaviour(PipeBehaviour behaviour) {
    runtime.setBehaviour(behaviour);
  }

  // ---- アイテム輸送 ----

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    for (Direction side : Direction.values()) {
      var pluggable = getPipePluggable(side);
      if (pluggable != null) pluggable.update(this, side);
    }
    runtime.tick(level, pos, state);
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
    injectItemWithSpeed(stack, from, runtime.material().getItemSpeed());
  }

  public void injectItemWithSpeed(ItemStack stack, Direction from, float speed) {
    runtime.injectItem(stack, from, speed);
  }

  public void dropItems() {
    runtime.dropItems();
  }

  // ---- Fluid ----

  public List<TravelingItem> getTravelingItems() {
    return runtime.travelingItems();
  }

  @Nullable
  public FluidTank getFluidTank() {
    return runtime.fluidTank();
  }

  @Nullable
  public FluidTransportModule getFluidTransportModule() {
    return runtime.fluidTransport();
  }

  public int getFluidRoundRobinIndex() {
    return runtime.fluidRoundRobinIndex();
  }

  // ---- Energy ----

  public void advanceFluidRoundRobin(int size) {
    runtime.advanceFluidRoundRobin(size);
  }

  @Nullable
  public EnergyTransportModule getEnergyTransportModule() {
    return runtime.energyTransport();
  }

  @Nullable
  public EnergyStorage getPipeEnergyStorage() {
    return runtime.energyStorage();
  }

  // ---- Wire signals ----

  public int getPipeEnergyStored() {
    EnergyStorage storage = runtime.energyStorage();
    return storage != null ? storage.getEnergyStored() : 0;
  }

  public void setWireSignal(DyeColor color, boolean signal) {
    runtime.setWireSignal(color, signal);
  }

  // ---- Getters / Setters ----

  public PipeType getTransportType() {
    return runtime.type();
  }

  public PipeMaterial getPipeMaterial() {
    return runtime.material();
  }

  public int getIronPipeEnergyLimit() {
    return runtime.ironPipeEnergyLimit();
  }

  public void setIronPipeEnergyLimit(int ironPipeEnergyLimit) {
    runtime.setIronPipeEnergyLimit(ironPipeEnergyLimit);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public Direction getIronPipeOutput() {
    return runtime.ironPipeOutput();
  }

  public void setIronPipeOutput(Direction ironPipeOutput) {
    runtime.setIronPipeOutput(ironPipeOutput);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public Direction getExtractionSide() {
    return runtime.extractionSide();
  }

  public void setExtractionSide(Direction extractionSide) {
    runtime.setExtractionSide(extractionSide);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public int getFilterSlotIndex() {
    return runtime.filterSlotIndex();
  }

  public void setFilterSlotIndex(int filterSlotIndex) {
    runtime.setFilterSlotIndex(filterSlotIndex);
  }

  public Map<Direction, SimpleInventory> getFilters() {
    return runtime.filters();
  }

  public SimpleInventory getFilter(Direction direction) {
    return runtime.filter(direction);
  }

  public long getUsedFilters() {
    return runtime.usedFilters();
  }

  public void setUsedFilters(long usedFilters) {
    runtime.setUsedFilters(usedFilters);
    setChanged();
  }

  public @Nullable DyeColor getPipeColor() {
    return runtime.pipeColor();
  }

  public void setPipeColor(@Nullable DyeColor color) {
    runtime.setPipeColor(color);
    setChanged();
    Level level = getLevel();
    BlockPos pos = getBlockPos();
    if (!level.isClientSide) {
      level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }
  }

  public ExtractFilterMode getExtractFilterMode() {
    return runtime.extractFilterMode();
  }

  // ---- NBT ----

  public void setExtractFilterMode(ExtractFilterMode extractFilterMode) {
    runtime.setExtractFilterMode(extractFilterMode);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

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
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return runtime.type() == PipeType.ITEM;
  }
  // --- End Container Implementation ---

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    PipeType oldType = runtime.type();
    PipeMaterial oldMaterial = runtime.material();
    runtime.load(tag);
    if (runtime.type() != oldType || runtime.material() != oldMaterial) {
      rebuildCapabilities();
      configureExtractionBattery();
      if (getBattery() != null && tag.contains("battery")) {
        getBattery().read(tag.getCompound("battery"));
      }
    }
    if (tag.contains("SideProperties")) {
      sideProperties.readFromNBT(tag.getCompound("SideProperties"));
    }
  }

  // ---- Capabilities ----

  @Override
  protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    runtime.save(tag);
    CompoundTag sideTag = new CompoundTag();
    sideProperties.writeToNBT(sideTag);
    tag.put("SideProperties", sideTag);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER && runtime.type() == PipeType.ITEM) {
      return itemHandlerCap.cast();
    }
    if (cap == ForgeCapabilities.FLUID_HANDLER && runtime.type() == PipeType.FLUID) {
      return (side == null ? fluidHandlerCap : fluidSideCaps.get(side)).cast();
    }
    if (cap == ForgeCapabilities.ENERGY && runtime.type() == PipeType.ENERGY) {
      if (side != null) {
        if (!canTransferEnergy(side)) return LazyOptional.empty();
        return energySideCapsMap.get(side).cast();
      }
      // Transport needs an incoming face; the legacy standalone buffer is not routed.
      return LazyOptional.empty();
    }
    // Powered pipe (for extract)
    if (cap == ForgeCapabilities.ENERGY && runtime.material() == PipeMaterial.WOOD) {
      var battery = getBattery();
      if (battery != null) {
        return LazyOptional.of(() -> battery).cast();
      }
    }
    return super.getCapability(cap, side);
  }

  // ---- Network ----

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemHandlerCap.invalidate();
    fluidHandlerCap.invalidate();
    fluidSideCaps.values().forEach(LazyOptional::invalidate);
    energyCap.invalidate();
    for (var cap : energySideCapsMap.values()) {
      if (cap != null) cap.invalidate();
    }
  }

  public Item getPipeItem() {
    RegistryObject<PipeBlock> block = TransportBlocks.PIPES.get(runtime.type(), runtime.material());
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

    if (pluggable != null) pluggable.onAttachedPipe(this, direction);

    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

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
    setChanged();
  }

  @Override
  public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from) {
    return injectItem(stack, doAdd, from, null);
  }

  @Override
  public PipeType getPipeType() {
    return runtime.type();
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

  /** Shared by energy capabilities, demand propagation and actual transfers. */
  public boolean canTransferEnergy(Direction side) {
    if (side == null || runtime.type() != PipeType.ENERGY || isRemoved() || hasBlockingPluggable(side)) return false;
    if (level == null) return true;
    BlockPos neighborPos = worldPosition.relative(side);
    if (!level.hasChunkAt(neighborPos)) return false;
    BlockState neighborState = level.getBlockState(neighborPos);
    PipeBehaviour behaviour = runtime.behaviour();
    if (behaviour != null && !behaviour.canConnectTo(this, side, neighborState)) return false;
    if (neighborState.getBlock() instanceof PipeBlock otherBlock && otherBlock.getTransportType() != PipeType.ENERGY) return false;
    if (level.getBlockEntity(neighborPos) instanceof PipeBlockEntity other) {
      PipeBehaviour otherBehaviour = other.runtime.behaviour();
      return other.runtime.type() == PipeType.ENERGY && !other.isRemoved()
        && !other.hasBlockingPluggable(side.getOpposite())
        && (otherBehaviour == null || otherBehaviour.canConnectTo(other, side.getOpposite(), getBlockState()));
    }
    return true;
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
    runtime.setPipeColor(color);
    setChanged();
    level.sendBlockUpdated(pos, state, state, 3);
    return true;
  }

  @Override
  public boolean canInjectItems(@Nullable Direction from) {
    return runtime.type() == PipeType.ITEM;
  }

  @Override
  public int injectItem(ItemStack stack, boolean doAdd, @Nullable Direction from, @Nullable Integer color) {
    if (runtime.type() != PipeType.ITEM) {
      return 0;
    }
    if (doAdd) {
      injectItem(stack, from);
    }
    return stack.getCount();
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    info.add("Type      : " + getPipeType().getSerializedName());
    info.add("Material  : " + runtime.material().getSerializedName());
    if (runtime.pipeColor() != null) {
      info.add("Color     : " + runtime.pipeColor());
    }
    if (getPipeType() == PipeType.ITEM) {
      info.add("Traveling Items");
      runtime.travelingItems().forEach(it -> {
        info.add("  Item: " + it.getStack());
        info.add("  Direction: " + it.getNextDirection().getSerializedName().toUpperCase());
      });
    } else if (getPipeType() == PipeType.FLUID) {
      info.add("Traveling Fluids");
    } else {
      EnergyStorage energyStorage = runtime.energyStorage();
      EnergyTransportModule energyTransportModule = runtime.energyTransport();
      if (energyStorage == null || energyTransportModule == null) return;
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

          pluggable.writeTag(pluggableData);
          nbt.put(key, pluggableData);
        }
      }
    }

    public void readFromNBT(CompoundTag nbt) {
      for (int i = 0; i < Direction.values().length; i++) {
        final String key = "pluggable[" + i + "]";
        if (!nbt.contains(key)) {
          pluggables[i] = null;
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
