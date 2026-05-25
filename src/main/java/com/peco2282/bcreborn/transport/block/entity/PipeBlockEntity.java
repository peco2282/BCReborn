package com.peco2282.bcreborn.transport.block.entity;

import com.peco2282.bcreborn.api.blocks.IColoredBlock;
import com.peco2282.bcreborn.api.transport.IPipeTile;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.transport.BlocksTransport;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.transport.BlockEntityTypesTransport;
import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import com.peco2282.bcreborn.transport.pipe.TravelingItem;
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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
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
public class PipeBlockEntity extends BuildCraftBlockEntity {
  // アイテム輸送
  private final ItemTransportModule itemTransportModule = new ItemTransportModule(this);
  // 流体輸送モジュール（FLUID パイプのみ有効）
  @Nullable
  private final FluidTransportModule fluidTransportModule;
  // エネルギー輸送モジュール（ENERGY パイプのみ有効）
  @Nullable
  private final EnergyTransportModule energyTransportModule;
  // 流体ストレージ（FLUID パイプのみ生成）
  @Nullable
  private final FluidTank fluidTank;
  // エネルギーストレージ（ENERGY パイプのみ生成）
  @Nullable
  private final EnergyStorage energyStorage;
  private final EnumMap<Direction, SimpleInventory> filters = new EnumMap<>(Direction.class);
  // Capability lazy optionals
  private final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> new PipeItemHandler(this, null));
  private final LazyOptional<IFluidHandler> fluidHandlerCap;
  private final LazyOptional<IEnergyStorage> energyCap;
  // 流体流入方向を記録するためのラッパー（逆流防止用）
  @Nullable
  private PipeFluidHandler pipeFluidHandler = null;
  private final boolean[] wireSignals = new boolean[4];
  protected PipeType transportType;
  protected PipeMaterial pipeMaterial;
  protected int ticksSincePull = 0;
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
  private int pipeColor = 0;

  public final SideProperties sideProperties = new SideProperties();

  public PipeBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, PipeType.ITEM, PipeMaterial.IRON);
  }

  public PipeBlockEntity(BlockPos pos, BlockState state, @NotNull PipeType type, @NotNull PipeMaterial material) {
    super(getBlockEntityType(type), pos, state);
    if (material.unsupports(type)) {
      throw new IllegalArgumentException("Pipe material does not support the specified pipe type");
    }
    this.transportType = type;
    this.pipeMaterial = material;
    this.fluidTank = (type == PipeType.FLUID) ? new FluidTank(1000) : null;
    int energyCap = (type == PipeType.ENERGY) ? material.getEnergyTransferRate() * 2 : -1;
    this.energyStorage = (type == PipeType.ENERGY) ? new EnergyStorage(energyCap, energyCap, energyCap, 0) : null;
    this.fluidTransportModule = (type == PipeType.FLUID) ? new FluidTransportModule(this) : null;
    this.energyTransportModule = (type == PipeType.ENERGY) ? new EnergyTransportModule(this) : null;
    if (fluidTank != null) {
      this.pipeFluidHandler = new PipeFluidHandler(this, fluidTank);
      this.fluidHandlerCap = LazyOptional.of(() -> pipeFluidHandler);
    } else {
      this.fluidHandlerCap = LazyOptional.empty();
    }
    this.energyCap = (energyStorage != null) ? LazyOptional.of(() -> energyStorage) : LazyOptional.empty();
    for (Direction dir : Direction.values()) {
      filters.put(dir, new SimpleInventory(9, "Filter " + dir.name(), 1));
    }
    this.behaviour = PipeBehaviourManager.getBehaviour(type, material);
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
    if (level.isClientSide) return;
    ticksSincePull++;

    if (behaviour != null) {
      behaviour.tick(this, level, pos, state);
    }

    if (transportType == PipeType.ITEM) {
      tickItems(level, pos);
    } else if (transportType == PipeType.FLUID) {
      if (fluidTransportModule != null) {
        fluidTransportModule.tick(level, pos);
      }
    } else if (transportType == PipeType.ENERGY) {
      // エネルギー吸い出し（Wood パイプ等）を先に実行してからモジュールをtick
      if (behaviour instanceof com.peco2282.bcreborn.transport.pipe.behaviour.EnergyPipeBehaviour eb) {
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
    int totalEnergy = 0;
    for (Direction dir : Direction.values()) {
      BlockEntity be = getBlockEntity(worldPosition.relative(dir));
      if (be instanceof EngineBlockEntity<?> engine) {
        if (engine.orientation == dir.getOpposite() && engine.isActive()) {
          totalEnergy += engine.getEnergyStored();
        }
      }
    }
    return totalEnergy;
  }

  public void consumeExtractionEnergy(int amount) {
    int remaining = amount;
    for (Direction dir : Direction.values()) {
      if (remaining <= 0) break;
      BlockEntity be = getBlockEntity(worldPosition.relative(dir));
      if (be instanceof EngineBlockEntity<?> engine) {
        if (engine.orientation == dir.getOpposite() && engine.isActive()) {
          int stored = engine.getEnergyStored();
          int toConsume = Math.min(remaining, stored);
          engine.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(e -> {
            e.extractEnergy(toConsume, false);
          });
          remaining -= toConsume;
        }
      }
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

  private int getWireIndex(DyeColor color) {
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

  public int getPipeColor() {
    return pipeColor;
  }

  public void setPipeColor(int color) {
    this.pipeColor = color & 0xF;
    setChanged();
    Level level = getLevel();
    BlockPos pos = getBlockPos();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }
  }

  // ---- NBT ----

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
    if (tag.contains("TransportType")) {
      this.transportType = PipeType.valueOf(tag.getString("TransportType"));
    }
    if (tag.contains("PipeMaterial")) {
      this.pipeMaterial = PipeMaterial.valueOf(tag.getString("PipeMaterial").toUpperCase());
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
      this.pipeColor = tag.getInt("PipeColor");
    }
    if (tag.contains("Filters")) {
      ListTag filtersTag = tag.getList("Filters", Tag.TAG_COMPOUND);
      for (int i = 0; i < filtersTag.size(); i++) {
        CompoundTag entry = filtersTag.getCompound(i);
        if (entry.contains("Dir")) {
          Direction dir = Direction.from3DDataValue(entry.getInt("Dir"));
          filters.get(dir).read(entry);
        } else if (i < 6) {
          // legacy fallback: index-based
          filters.get(Direction.from3DDataValue(i)).read(entry);
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
      energyStorage.deserializeNBT(tag.get("Energy"));
    }
  }

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
      filters.get(dir).write(filterTag);
      filtersTag.add(filterTag);
    }
    tag.put("Filters", filtersTag);
    tag.putLong("usedFilters", usedFilters);
    tag.putInt("PipeColor", pipeColor);

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
      tag.put("Energy", energyStorage.serializeNBT());
    }
  }

  // ---- Capabilities ----

  @NotNull
  @Override
  public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
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
      return energyCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemHandlerCap.invalidate();
    fluidHandlerCap.invalidate();
    energyCap.invalidate();
  }

  // ---- Network ----

  @Nullable
  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public CompoundTag getUpdateTag() {
    return saveWithoutMetadata();
  }

  public static BlockEntityType<PipeBlockEntity> getBlockEntityType(PipeType type) {
    return switch (type) {
      case ITEM -> BlockEntityTypesTransport.ITEM_PIPE.get();
      case FLUID -> BlockEntityTypesTransport.FLUID_PIPE.get();
      case ENERGY -> BlockEntityTypesTransport.ENERGY_PIPE.get();
    };
  }
  public Item getPipeItem() {
    RegistryObject<PipeBlock> block = BlocksTransport.PIPES_BY_MAT.get(pipeMaterial).get(transportType);
    if (block != null) {
      return block.get().asItem();
    }
    return Items.AIR;
  }

  public ArrayList<ItemStack> computeItemDrop() {
    ArrayList<ItemStack> list = new ArrayList<>();
    // Pluggables
    for (PipePluggable pluggable : sideProperties.pluggables) {
      if (pluggable != null) {
        Collections.addAll(list, pluggable.getDropItems(new IPipeTile() {
          @Override
          public PipeType getPipeType() {
            return switch (transportType) {
              case ITEM -> PipeType.ITEM;
              case FLUID -> PipeType.FLUID;
              case ENERGY -> PipeType.POWER;
            };
          }

          @Override
          public Level getWorld() {
            return level;
          }

          @Override
          public BlockPos getPos() {
            return worldPosition;
          }

          @Override
          public boolean isPipeConnected(Direction with) {
            return false;
          }

          @Override
          public net.minecraft.world.level.block.Block getNeighborBlock(Direction dir) {
            return level.getBlockState(worldPosition.relative(dir)).getBlock();
          }

          @Override
          public BlockEntity getNeighborTile(Direction dir) {
            return level.getBlockEntity(worldPosition.relative(dir));
          }

          @Override
          public com.peco2282.bcreborn.api.transport.IPipe getNeighborPipe(Direction dir) {
            return null;
          }

          @Override
          public com.peco2282.bcreborn.api.transport.IPipe getPipe() {
            return null;
          }

          @Override
          public int getPipeColor() {
            return pipeColor;
          }

          @Override
          public PipePluggable getPipePluggable(Direction direction) {
            return sideProperties.pluggables[direction.ordinal()];
          }

          @Override
          public boolean hasPipePluggable(Direction direction) {
            return sideProperties.pluggables[direction.ordinal()] != null;
          }

          @Override
          public boolean hasBlockingPluggable(Direction direction) {
            PipePluggable p = getPipePluggable(direction);
            return p != null && p.isBlocking(this, direction);
          }

          @Override
          public void scheduleNeighborChange() {
          }

          @Override
          public void scheduleRenderUpdate() {
          }

          @Override
          public int injectItem(ItemStack stack, boolean doAdd, Direction from, Integer color) {
            if (doAdd) {
              PipeBlockEntity.this.injectItem(stack, from);
            }
            return stack.getCount();
          }

          @Override
          public int injectItem(ItemStack stack, boolean doAdd, Direction from) {
            return injectItem(stack, doAdd, from, null);
          }

          @Override
          public boolean canInjectItems(Direction from) {
            return true;
          }
        }));
      }
    }
    return list;
  }

  public static class SideProperties {
		public PipePluggable[] pluggables = new PipePluggable[Direction.values().length];

		public void writeToNBT(CompoundTag nbt) {
			for (int i = 0; i < Direction.values().length; i++) {
				PipePluggable pluggable = pluggables[i];
				final String key = "pluggable[" + i + "]";
				if (pluggable == null) {
					nbt.remove(key);
				} else {
					CompoundTag pluggableData = new CompoundTag();
					String name = PipeManager.getPluggableName(pluggable.getClass());
					if (name != null) {
						pluggableData.putString("pluggableName", name);
					}
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
				try {
					CompoundTag pluggableData = nbt.getCompound(key);
					Class<?> pluggableClass = PipeManager.getPluggableByName(pluggableData.getString("pluggableName"));

					if (pluggableClass != null && PipePluggable.class.isAssignableFrom(pluggableClass)) {
						PipePluggable pluggable = (PipePluggable) pluggableClass.getDeclaredConstructor().newInstance();
						pluggable.readFromNBT(pluggableData);
						pluggables[i] = pluggable;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void rotateLeft() {
			PipePluggable[] newPluggables = new PipePluggable[Direction.values().length];
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
