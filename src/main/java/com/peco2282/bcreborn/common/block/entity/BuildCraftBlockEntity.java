package com.peco2282.bcreborn.common.block.entity;

import com.peco2282.bcreborn.api.IControllable;
import com.peco2282.bcreborn.api.core.ISerializable;
import com.peco2282.bcreborn.api.energy.IEnergyHandler;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BuildCraftBlockEntity extends BlockEntity implements IEnergyHandler, ISerializable {
  private boolean init = false;
  protected IControllable.Mode mode;

  private int receivedTick, extractedTick;
  private long worldTimeEnergyReceive;
  private EnergyStorage battery;

  protected int xCoord, yCoord, zCoord;

  public BuildCraftBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
    this.xCoord = p_155229_.getX();
    this.yCoord = p_155229_.getY();
    this.zCoord = p_155229_.getZ();
  }

  public @NotNull BlockPos getBlockPos() {
    return this.worldPosition;
  }

  public @NotNull Level getLevel() {
    //noinspection DataFlowIssue
    return super.level;
  }

  public void initialize() {
  }

  protected BlockState getBlockState(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.getBlockState(pos);
  }

  protected BlockEntity getBlockEntity(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.getBlockEntity(pos);
  }

  protected boolean hasNeighborSignal(BlockPos pos) {
    //noinspection DataFlowIssue
    return level.hasNeighborSignal(pos);
  }

  @Override
  public void setRemoved() {
    init = false;
    super.setRemoved();
  }

  protected abstract void tick(Level level, BlockPos pos, BlockState state);

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    if (battery != null) {
      CompoundTag batteryNBT = new CompoundTag();
      battery.write(batteryNBT);
      nbt.put("battery", batteryNBT);
    }
    if (mode != null) {
      nbt.putByte("lastMode", (byte) mode.ordinal());
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    if (battery != null) {
      battery.read(nbt.getCompound("battery"));
    }
    if (nbt.contains("lastMode")) {
      mode = IControllable.Mode.values()[nbt.getByte("lastMode")];
    }
  }

  protected int getTicksSinceEnergyReceived() {
    return (int) (level.getGameTime() - worldTimeEnergyReceive);
  }


  @Override
  public int hashCode() {
    var pos = getBlockPos();
    return (pos.getX() * 37 + pos.getY()) * 37 + pos.getZ();
  }

  @Override
  public void readData(FriendlyByteBuf data) {
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
  }


  public static <T extends BuildCraftBlockEntity> BlockEntityTicker<T> ticker() {
    return (level, blockPos, blockState, blockEntity) -> {
      if (blockEntity instanceof BuildCraftBlockEntity) {
        blockEntity.tick(level, blockPos, blockState);
      } else {
        throw new IllegalStateException("BlockEntity is not an instance of BuildCraftBlockEntity!");
      }
    };
  }

  protected static ResourceBuilder getResource(String path) {
    return ResourceBuilder.create(path);
  }

  protected static ResourceBuilder getResource(String ns, String path) {
    return ResourceBuilder.create(ns, path);
  }

  protected static ResourceBuilder getChildResource(ResourceLocation root, String path) {
    return ResourceBuilder.create(root.getNamespace(), root.getPath()).addPath(path);
  }

  protected static ResourceBuilder getChildResource(ResourceLocation root, ResourceLocation child) {
    if (root.getNamespace().equals(child.getNamespace())) {
      return getChildResource(root, child.getPath());
    } else {
      throw new IllegalArgumentException("Cannot get child resource from different namespaces: " + root.getNamespace() + " apply " + child.getNamespace() + "!");
    }
  }

}
