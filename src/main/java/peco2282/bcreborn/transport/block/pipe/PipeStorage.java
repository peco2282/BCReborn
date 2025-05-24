/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.world.item.ItemStack;
import peco2282.bcreborn.transport.block.*;
import peco2282.bcreborn.transport.block.PipeFluidBlock;
import peco2282.bcreborn.transport.block.PipeItemBlock;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

import java.util.*;
import java.util.function.Function;

/**
 * Represents a base storage implementation for pipes within the system. This abstract class defines
 * common behavior for different types of pipe storages. Subtypes include {@link
 * PipeStorage.ItemStorage}, {@link PipeStorage.FluidStorage}, and {@link
 * PipeStorage.EnergyStorage}.
 */
public abstract sealed class PipeStorage<E extends Entity>
    permits PipeStorage.ItemStorage, PipeStorage.FluidStorage, PipeStorage.EnergyStorage {
  protected final BasePipeBlockEntity pipe;

  private PipeStorage(BasePipeBlockEntity pipe) {
    this.pipe = pipe;
  }

  /**
   * Factory method to create the appropriate type of {@link PipeStorage} based on the provided
   * {@link BasePipeBlockEntity}'s pipe type.
   *
   * @param pipe the pipe block entity to associate with the storage
   * @return the corresponding {@link PipeStorage} subtype for the pipe type
   */
  public static PipeStorage<?> create(BasePipeBlockEntity pipe) {
    return switch (pipe.getPipeType()) {
      case ITEM -> new ItemStorage(pipe);
      case FLUID -> new FluidStorage(pipe);
      case ENERGY -> new EnergyStorage(pipe);
    };
  }

  /**
   * Retrieves the {@link BCBasePipeBlock} associated with the current pipe storage.
   *
   * @return the {@link BCBasePipeBlock} associated with this storage
   */
  public final BCBasePipeBlock getPipe() {
    return (BCBasePipeBlock) pipe.getBlockState().getBlock();
  }

  public abstract boolean isEmpty();

  public abstract int size();

  public abstract E removeFirst();

  public abstract void add(E item);

  public <T> void add(T t, Function<T, E> function) {
    add(function.apply(t));
  }

  public void addAll(List<E> items) {
    for (E item : items) add(item);
  }

  public <T> void addAll(List<T> items, Function<T, E> function) {
    for (T item : items) add(item, function);
  }

  public abstract void remove(E item);

  public abstract void clear();

  public void removeAll(List<E> items) {
    for (E item : items) remove(item);
  }

  public int capacity() {
    return 10;
  }

  public abstract List<E> copy();

  /**
   * Retrieves the current pipe and casts it to {@link PipeItemBlock}. This is used specifically for
   * item-related pipe operations.
   *
   * @return the pipe cast to {@link PipeItemBlock}
   * @throws UnsupportedOperationException if this is not an item pipe type
   */
  public PipeItemBlock asItemPipe() {
    throw uoe("item pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link ItemStorage} for performing item-specific storage
   * operations.
   *
   * @return the current storage cast to {@link ItemStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link ItemStorage}
   */
  public ItemStorage asItemStorage() {
    throw uoe("item storage");
  }

  /**
   * Retrieves the current pipe and casts it to {@link PipeFluidBlock}. This is used specifically
   * for fluid-related pipe handling.
   *
   * @return the pipe cast to {@link PipeFluidBlock}
   * @throws UnsupportedOperationException if this is not a fluid pipe type
   */
  public PipeFluidBlock asFluidPipe() {
    throw uoe("fluid pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link FluidStorage} for accessing fluid-specific behaviors.
   *
   * @return the current storage cast to {@link FluidStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link FluidStorage}
   */
  public FluidStorage asFluidStorage() {
    throw uoe("fluid storage");
  }

  /**
   * Retrieves the current pipe and casts it to {@link PipeEnergyBlock}. This is used specifically
   * for energy-related pipe handling.
   *
   * @return the pipe cast to {@link PipeEnergyBlock}
   * @throws UnsupportedOperationException if this is not an energy pipe type
   */
  public PipeEnergyBlock asEnergyPipe() {
    throw uoe("energy pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link EnergyStorage} for performing energy-specific storage
   * operations.
   *
   * @return the current storage cast to {@link EnergyStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link EnergyStorage}
   */
  public EnergyStorage asEnergyStorage() {
    throw uoe("energy storage");
  }

  /**
   * Constructs an exception for unsupported operations based on the given context string.
   *
   * @param s the context of the unsupported operation
   * @return a {@link RuntimeException} detailing the unsupported operation
   */
  /**
   * Constructs an exception for unsupported operations based on the given context string.
   *
   * @param s the context of the unsupported operation
   * @return a {@link RuntimeException} detailing the unsupported operation
   */
  private RuntimeException uoe(String s) {
    return new UnsupportedOperationException("Unsupported yet as " + s + ".");
  }

  protected void capacityAssersion(int size, String type) {
    if (capacity() < size) {
      throw new OverflowStrageException(capacity(), size, type);
    }
  }

  /**
   * Represents an item-based pipe storage. This class provides specific implementations for
   * interacting with item pipes.
   */
  public static final class ItemStorage extends PipeStorage<ItemEntity> {
    private final IntObjectMap<ItemEntity> container = new IntObjectHashMap<>();

    /**
     * Constructs a new {@link ItemStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public ItemStorage(BasePipeBlockEntity pipe) {
      super(pipe);
    }

    @Override
    public boolean isEmpty() {
      return container.isEmpty();
    }

    @Override
    public int size() {
      return container.size();
    }

    @Override
    public ItemEntity removeFirst() {
      if (isEmpty()) return null;
      else {
        int key = container.keySet().iterator().next();
        return container.remove(key);
      }
    }

    public void add(ItemStack stack) {
      this.add(ItemEntity.of(stack));
    }

    public void add(int index, ItemStack stack) {
      container.computeIfAbsent(index, s -> ItemEntity.of(stack));
    }

    @Override
    public void add(ItemEntity item) {
      capacityAssersion(size(), "Item");
      container.put(size(), item);
    }

    @Override
    public void remove(ItemEntity item) {
      for (IntObjectMap.PrimitiveEntry<ItemEntity> entry : container.entries()) {
        if (entry.value() == item) {
          container.remove(entry.key());
        }
      }
    }

    @Override
    public void clear() {
      container.clear();
    }

    @Override
    public int capacity() {
      return 100;
    }

    /**
     * Resolves the pipe as a {@link PipeItemBlock}.
     *
     * @return the pipe as {@link PipeItemBlock}
     */
    @Override
    public PipeItemBlock asItemPipe() {
      return (PipeItemBlock) pipe.getBlockState().getBlock();
    }

    @Override
    public PipeStorage.ItemStorage asItemStorage() {
      return this;
    }

    @Override
    public List<ItemEntity> copy() {
      return new ArrayList<>(container.values());
    }
  }

  /**
   * Represents a fluid-based pipe storage. This class provides specific implementations for
   * interacting with fluid pipes.
   */
  public static final class FluidStorage extends PipeStorage<FluidEntity> {
    private final Queue<FluidEntity> fluids = new ArrayDeque<>(capacity());

    /**
     * Constructs a new {@link FluidStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public FluidStorage(BasePipeBlockEntity pipe) {
      super(pipe);
    }

    @Override
    public boolean isEmpty() {
      return fluids.isEmpty();
    }

    @Override
    public int size() {
      return fluids.size();
    }

    @Override
    public FluidEntity removeFirst() {
      return fluids.poll();
    }

    @Override
    public void add(FluidEntity item) {
      capacityAssersion(size(), "Fluid");
      fluids.add(item);
    }

    @Override
    public void remove(FluidEntity item) {
      fluids.remove(item);
    }

    @Override
    public void clear() {
      fluids.clear();
    }

    @Override
    public PipeFluidBlock asFluidPipe() {
      return (PipeFluidBlock) pipe.getBlockState().getBlock();
    }

    @Override
    public PipeStorage.FluidStorage asFluidStorage() {
      return this;
    }

    @Override
    public List<FluidEntity> copy() {
      return new ArrayList<>(fluids);
    }
  }

  /**
   * Represents an energy-based pipe storage. This class provides specific implementations for
   * interacting with energy pipes.
   */
  public static final class EnergyStorage extends PipeStorage<EnergyEntity> {
    private final Queue<EnergyEntity> energies = new ArrayDeque<>(capacity());

    /**
     * Constructs a new {@link EnergyStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public EnergyStorage(BasePipeBlockEntity pipe) {
      super(pipe);
    }

    @Override
    public boolean isEmpty() {
      return energies.isEmpty();
    }

    @Override
    public int size() {
      return energies.size();
    }

    @Override
    public EnergyEntity removeFirst() {
      return energies.poll();
    }

    @Override
    public void add(EnergyEntity item) {
      capacityAssersion(size(), "Energy");
      energies.add(item);
    }

    @Override
    public void remove(EnergyEntity item) {
      energies.remove(item);
    }

    @Override
    public void clear() {
      energies.clear();
    }

    @Override
    public PipeEnergyBlock asEnergyPipe() {
      return (PipeEnergyBlock) pipe.getBlockState().getBlock();
    }

    @Override
    public PipeStorage.EnergyStorage asEnergyStorage() {
      return this;
    }

    @Override
    public List<EnergyEntity> copy() {
      return new ArrayList<>(energies);
    }
  }
}
