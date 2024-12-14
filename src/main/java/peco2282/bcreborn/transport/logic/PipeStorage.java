package peco2282.bcreborn.transport.logic;

import peco2282.bcreborn.transport.block.BasePipeBlock;
import peco2282.bcreborn.transport.block.PipeEnergyBlock;
import peco2282.bcreborn.transport.block.PipeFluidBlock;
import peco2282.bcreborn.transport.block.PipeItemBlock;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;


/**
 * Represents a base storage implementation for pipes within the system.
 * This abstract class defines common behavior for different types of pipe storages.
 * Subtypes include {@link PipeStorage.ItemStorage}, {@link PipeStorage.FluidStorage}, and {@link PipeStorage.EnergyStorage}.
 */
public abstract sealed class PipeStorage permits PipeStorage.ItemStorage, PipeStorage.FluidStorage, PipeStorage.EnergyStorage {
  protected final BasePipeBlockEntity pipe;

  private PipeStorage(BasePipeBlockEntity pipe) {
    this.pipe = pipe;
  }

  /**
   * Factory method to create the appropriate type of {@link PipeStorage}
   * based on the provided {@link BasePipeBlockEntity}'s pipe type.
   *
   * @param pipe the pipe block entity to associate with the storage
   * @return the corresponding {@link PipeStorage} subtype for the pipe type
   */
  public static PipeStorage create(BasePipeBlockEntity pipe) {
    return switch (pipe.getPipeType()) {
      case ITEM -> new ItemStorage(pipe);
      case FLUID -> new FluidStorage(pipe);
      case ENERGY -> new EnergyStorage(pipe);
    };
  }


  /**
   * Retrieves the {@link BasePipeBlock} associated with the current pipe storage.
   *
   * @return the {@link BasePipeBlock} associated with this storage
   */
  public final BasePipeBlock getPipe() {
    return (BasePipeBlock) pipe.getBlockState().getBlock();
  }

  /**
   * Retrieves the current pipe and casts it to {@link PipeItemBlock}.
   * This is used specifically for item-related pipe operations.
   *
   * @return the pipe cast to {@link PipeItemBlock}
   * @throws UnsupportedOperationException if this is not an item pipe type
   */
  public PipeItemBlock asItemPipe() {
    throw nie("item pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link ItemStorage} for performing item-specific
   * storage operations.
   *
   * @return the current storage cast to {@link ItemStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link ItemStorage}
   */
  public ItemStorage asItemStorage() {
    throw nie("item storage");
  }

  
  /**
   * Retrieves the current pipe and casts it to {@link PipeFluidBlock}.
   * This is used specifically for fluid-related pipe handling.
   *
   * @return the pipe cast to {@link PipeFluidBlock}
   * @throws UnsupportedOperationException if this is not a fluid pipe type
   */
  public PipeFluidBlock asFluidPipe() {
    throw nie("fluid pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link FluidStorage} for accessing
   * fluid-specific behaviors.
   *
   * @return the current storage cast to {@link FluidStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link FluidStorage}
   */
  public FluidStorage asFluidStorage() {
    throw nie("fluid storage");
  }

  /**
   * Retrieves the current pipe and casts it to {@link PipeEnergyBlock}.
   * This is used specifically for energy-related pipe handling.
   *
   * @return the pipe cast to {@link PipeEnergyBlock}
   * @throws UnsupportedOperationException if this is not an energy pipe type
   */
  public PipeEnergyBlock asEnergyPipe() {
    throw nie("energy pipe");
  }

  /**
   * Casts the {@link PipeStorage} to {@link EnergyStorage} for performing
   * energy-specific storage operations.
   *
   * @return the current storage cast to {@link EnergyStorage}
   * @throws UnsupportedOperationException if this storage is not type {@link EnergyStorage}
   */
  public EnergyStorage asEnergyStorage() {
    throw nie("energy storage");
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
  private RuntimeException nie(String s) {
      return new UnsupportedOperationException("Unsupported yet as " + s + ".");
  }

  /**
   * Represents an item-based pipe storage.
   * This class provides specific implementations for interacting with item pipes.
   */
  public static final class ItemStorage extends PipeStorage {
    /**
     * Constructs a new {@link ItemStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public ItemStorage(BasePipeBlockEntity pipe) {
      super(pipe);
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
  }

  /**
   * Represents a fluid-based pipe storage.
   * This class provides specific implementations for interacting with fluid pipes.
   */
  public static final class FluidStorage extends PipeStorage {
    /**
     * Constructs a new {@link FluidStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public FluidStorage(BasePipeBlockEntity pipe) {
        super(pipe);
    }

    @Override
    public PipeFluidBlock asFluidPipe() {
      return (PipeFluidBlock) pipe.getBlockState().getBlock();
    }

    @Override
    public PipeStorage.FluidStorage asFluidStorage() {
      return this;
    }
  }

  /**
   * Represents an energy-based pipe storage.
   * This class provides specific implementations for interacting with energy pipes.
   */
  public static final class EnergyStorage extends PipeStorage {
    /**
     * Constructs a new {@link EnergyStorage} associated with the given {@link BasePipeBlockEntity}.
     *
     * @param pipe the pipe block entity to associate with this storage
     */
    public EnergyStorage(BasePipeBlockEntity pipe) {
        super(pipe);
    }

    @Override
    public PipeEnergyBlock asEnergyPipe() {
      return (PipeEnergyBlock) pipe.getBlockState().getBlock();
    }

    @Override
    public PipeStorage.EnergyStorage asEnergyStorage() {
      return this;
    }
  }
}
